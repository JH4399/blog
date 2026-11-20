package com.example.blog.auth;

import com.example.blog.common.RedisService;
import com.example.blog.config.AuthProperties;
import com.example.blog.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.HexFormat;
import java.util.regex.Pattern;

/** 随机 Token 登录态：Redis 仅保存摘要和用户 ID，不保存原始凭据。 */
@Component
@RequiredArgsConstructor
public class TokenManager {

    public static final String KEY_PREFIX = "login:token:";
    private static final Pattern TOKEN_PATTERN = Pattern.compile("[A-Za-z0-9_-]{43}");
    private static final SecureRandom RANDOM = new SecureRandom();

    private final RedisService redisService;
    private final AuthProperties authProperties;

    public IssuedToken issue(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("登录用户 ID 必须为正数");
        }
        long expiresIn = authProperties.getTokenTtlSeconds();
        try {
            for (int attempt = 0; attempt < 3; attempt++) {
                byte[] bytes = new byte[32];
                RANDOM.nextBytes(bytes);
                String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
                if (redisService.setIfAbsent(key(token), userId.toString(), Duration.ofSeconds(expiresIn))) {
                    return new IssuedToken(token, expiresIn);
                }
            }
        } catch (DataAccessException exception) {
            throw unavailable(exception);
        }
        throw new BusinessException(HttpStatus.SERVICE_UNAVAILABLE, "登录服务暂不可用，请稍后重试");
    }

    public Long requireUserId(String token) {
        requireValidToken(token);
        String value;
        try {
            value = redisService.get(key(token));
        } catch (DataAccessException exception) {
            throw unavailable(exception);
        }
        if (value == null) {
            throw BusinessException.unauthorized("登录已过期或令牌无效，请重新登录");
        }
        try {
            long userId = Long.parseLong(value);
            if (userId > 0) {
                return userId;
            }
        } catch (NumberFormatException ignored) {
            // 损坏的登录态不能被识别为有效用户，也不向客户端泄露缓存内容。
        }
        throw BusinessException.unauthorized("登录已过期或令牌无效，请重新登录");
    }

    public void revoke(String token) {
        requireValidToken(token);
        try {
            redisService.delete(key(token));
        } catch (DataAccessException exception) {
            throw unavailable(exception);
        }
    }

    /** HTTP 认证方案名称不区分大小写；只接受本服务生成的 Token 格式。 */
    public String resolveBearerToken(String authorization) {
        if (authorization == null || !authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            throw BusinessException.unauthorized("请提供有效的 Bearer Token");
        }
        String token = authorization.substring(7);
        requireValidToken(token);
        return token;
    }

    private static void requireValidToken(String token) {
        if (token == null || !TOKEN_PATTERN.matcher(token).matches()) {
            throw BusinessException.unauthorized("请提供有效的 Bearer Token");
        }
    }

    private static String key(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return KEY_PREFIX + HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("当前运行环境不支持 SHA-256", exception);
        }
    }

    private static BusinessException unavailable(DataAccessException exception) {
        return new BusinessException(HttpStatus.SERVICE_UNAVAILABLE, "登录服务暂不可用，请稍后重试", exception);
    }

    public record IssuedToken(String value, long expiresIn) {
    }
}
