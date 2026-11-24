package com.example.blog.component;

import com.example.blog.common.RedisService;
import com.example.blog.exception.BusinessException;
import com.example.blog.mapper.PostMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.UUID;

/** 文章浏览计数协调组件，统一管理 24 小时去重、数据库事务和 Redis 标记补偿。 */
@Component
@Slf4j
public class PostViewCounter {

    public static final String VIEW_KEY_PREFIX = "posts:view:";
    public static final Duration VIEW_WINDOW = Duration.ofHours(24);

    private final RedisService redisService;
    private final PostMapper postMapper;
    private final TransactionTemplate transaction;

    public PostViewCounter(RedisService redisService, PostMapper postMapper,
            PlatformTransactionManager transactionManager) {
        this.redisService = redisService;
        this.postMapper = postMapper;
        this.transaction = new TransactionTemplate(transactionManager);
    }

    /**
     * 同一文章、访客身份每 24 小时增加一次浏览量。首次计数返回 true，重复访问返回 false。
     * 身份须由调用方从已验证的用户 ID 或服务端 IP 构造。
     * 仅负责计数事务，不负责文章详情读取；事务结果未知时保留标记。
     * 使用 REQUIRED，若有外层事务，true 表示本次已执行更新，最终提交由外层决定。
     */
    public boolean countOnce(Long postId, String viewerIdentity) {
        Reservation reservation = new Reservation(postId, viewerIdentity);
        try {
            if (!redisService.setIfAbsent(reservation.key, reservation.value, VIEW_WINDOW)) {
                return false;
            }
        } catch (DataAccessException exception) {
            throw new BusinessException(HttpStatus.SERVICE_UNAVAILABLE, "浏览统计服务暂不可用，请稍后重试", exception);
        }

        try {
            transaction.executeWithoutResult(status -> {
                TransactionSynchronizationManager.registerSynchronization(reservation);
                reservation.registered = true;
                if (postMapper.incrementPublishedViewCount(postId) == 0) {
                    throw BusinessException.notFound("文章不存在或未公开");
                }
            });
            return true;
        } catch (RuntimeException exception) {
            // 尚未注册说明更新 SQL 未执行，可立即释放；否则等待事务最终完成。
            if (!reservation.registered) {
                reservation.release();
            }
            throw exception;
        }
    }

    /** 每次请求独有的标记及其事务回调，均在当前线程使用。 */
    private final class Reservation implements TransactionSynchronization {
        private final Long postId;
        private final String key;
        private final String value = UUID.randomUUID().toString();
        private boolean registered;

        private Reservation(Long postId, String identity) {
            this.postId = postId;
            this.key = VIEW_KEY_PREFIX + postId + ":" + digest(identity);
        }

        @Override
        public void afterCompletion(int status) {
            if (status == STATUS_ROLLED_BACK) {
                release();
            } else if (status == STATUS_UNKNOWN) {
                // 数据库可能已提交，保留标记避免重试重复计数。
                log.warn("postId={} result=TRANSACTION_UNKNOWN，保留浏览去重标记至到期", postId);
            }
        }

        private void release() {
            try {
                redisService.deleteIfValueMatches(key, value);
            } catch (RuntimeException exception) {
                log.warn("释放文章浏览去重标记失败，等待 TTL 到期，postId={}", postId, exception);
            }
        }
    }

    private static String digest(String identity) {
        try {
            String identitySHA;
            identitySHA = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(identity.getBytes(StandardCharsets.UTF_8)));
            log.info("此次加密后的SHA-256为：" + identitySHA);
            return identitySHA;
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("当前运行环境不支持 SHA-256", exception);
        }
    }
}
