package com.example.blog.common;

import com.example.blog.config.AuthProperties;
import com.example.blog.exception.BusinessException;
import com.example.blog.service.TokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.awaitility.Awaitility.await;

/** 可选真实 Redis 测试，只操作本测试创建的 key，绝不 FLUSHDB。运行方式见 docs/login.md。 */
@EnabledIfSystemProperty(named = "blog.test.redis", matches = "true")
class RedisIntegrationTests {

    private LettuceConnectionFactory connectionFactory;
    private RedisService redis;
    private final List<String> keys = new ArrayList<>();
    private final String prefix = "test:blog:" + UUID.randomUUID() + ":";

    @BeforeEach
    void connect() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(
                System.getProperty("blog.test.redis.host", "127.0.0.1"),
                Integer.getInteger("blog.test.redis.port", 6379));
        configuration.setDatabase(Integer.getInteger("blog.test.redis.database", 0));
        String username = System.getenv("REDIS_USERNAME");
        if (username != null && !username.isBlank()) {
            configuration.setUsername(username);
        }
        String password = System.getenv("REDIS_PASSWORD");
        if (password != null && !password.isEmpty()) {
            configuration.setPassword(password);
        }
        connectionFactory = new LettuceConnectionFactory(configuration,
                LettuceClientConfiguration.builder().commandTimeout(Duration.ofSeconds(2)).build());
        connectionFactory.afterPropertiesSet();
        connectionFactory.start();
        redis = new RedisService(new StringRedisTemplate(connectionFactory));
    }

    @AfterEach
    void cleanUp() {
        try {
            if (redis != null && !keys.isEmpty()) {
                redis.delete(keys);
            }
        } finally {
            if (connectionFactory != null) {
                connectionFactory.destroy();
            }
        }
    }

    @Test
    void supportsStringsAtomicNxExpiryAndCounters() {
        String value = track(prefix + "string");
        redis.set(value, "你好", Duration.ofSeconds(10));
        assertThat(redis.get(value)).isEqualTo("你好");
        assertThat(redis.hasKey(value)).isTrue();
        assertThat(redis.getExpire(value)).isBetween(1L, 10L);
        assertThat(redis.setIfAbsent(value, "replacement", Duration.ofSeconds(30))).isFalse();
        assertThat(redis.get(value)).isEqualTo("你好");
        assertThat(redis.expire(value, Duration.ofSeconds(20))).isTrue();
        assertThat(redis.getExpire(value)).isBetween(1L, 20L);
        assertThat(redis.delete(value)).isTrue();
        assertThat(redis.setIfAbsent(value, "new", Duration.ofSeconds(10))).isTrue();

        String count = track(prefix + "count");
        redis.set(count, "1");
        assertThat(redis.getExpire(count)).isEqualTo(-1L);
        assertThat(redis.increment(count, 3)).isEqualTo(4);
        assertThat(redis.increment(count, -2)).isEqualTo(2);
        assertThat(redis.delete(List.of(value, count))).isEqualTo(2);
        assertThat(redis.getExpire(value)).isEqualTo(-2L);
    }

    @Test
    void supportsStringHashesListsAndSets() {
        String hash = track(prefix + "hash");
        redis.hashPut(hash, "name", "小爱");
        assertThat(redis.hashGet(hash, "name")).isEqualTo("小爱");
        assertThat(redis.hashEntries(hash)).containsEntry("name", "小爱");
        assertThat(redis.hashDelete(hash, "name")).isEqualTo(1);

        String list = track(prefix + "list");
        redis.listRightPush(list, "first", "second");
        assertThat(redis.listRange(list, 0, -1)).containsExactly("first", "second");
        assertThat(redis.listLeftPop(list)).isEqualTo("first");

        String set = track(prefix + "set");
        assertThat(redis.setAdd(set, "a", "a", "b")).isEqualTo(2);
        assertThat(redis.setMembers(set)).containsExactlyInAnyOrder("a", "b");
        assertThat(redis.setRemove(set, "a")).isEqualTo(1);
    }

    @Test
    void realRedisExpiresAndRevokesLoginTokens() throws Exception {
        AuthProperties properties = new AuthProperties();
        properties.setTokenTtlSeconds(1);
        TokenService tokens = new TokenService(redis, properties);
        String token = tokens.issue(123L).value();
        String key = track(tokenKey(token));
        assertThat(redis.get(key)).isEqualTo("123");
        assertThat(tokens.requireUserId(token)).isEqualTo(123L);
        await().atMost(Duration.ofSeconds(4)).pollInterval(Duration.ofMillis(50)).untilAsserted(() -> {
            BusinessException exception = catchThrowableOfType(BusinessException.class,
                    () -> tokens.requireUserId(token));
            assertThat(exception).isNotNull();
            assertThat(exception.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        });

        properties.setTokenTtlSeconds(60);
        String another = tokens.issue(456L).value();
        track(tokenKey(another));
        tokens.revoke(another);
        BusinessException exception = catchThrowableOfType(BusinessException.class,
                () -> tokens.requireUserId(another));
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private String track(String key) {
        keys.add(key);
        return key;
    }

    private static String tokenKey(String token) throws Exception {
        return TokenService.KEY_PREFIX + HexFormat.of().formatHex(
                MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8)));
    }
}
