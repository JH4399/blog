package com.example.blog.service;

import com.example.blog.common.RedisService;
import com.example.blog.config.AuthProperties;
import com.example.blog.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TokenServiceTests {

    @Test
    void usesConfiguredExpiryAndRetriesCollisionWithoutOverwriting() {
        RedisService redis = mock(RedisService.class);
        AuthProperties properties = new AuthProperties();
        properties.setTokenTtlSeconds(60);
        when(redis.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(false, true);
        TokenService.IssuedToken token = new TokenService(redis, properties).issue(123L);
        assertThat(token.expiresIn()).isEqualTo(60);
        verify(redis, times(2)).setIfAbsent(anyString(), org.mockito.ArgumentMatchers.eq("123"),
                org.mockito.ArgumentMatchers.eq(Duration.ofSeconds(60)));
    }

    @Test
    void repeatedCollisionFailsInsteadOfReturningAnUnstoredToken() {
        RedisService redis = mock(RedisService.class);
        TokenService service = new TokenService(redis, new AuthProperties());
        BusinessException exception = catchThrowableOfType(BusinessException.class, () -> service.issue(123L));
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        verify(redis, times(3)).setIfAbsent(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void damagedRedisValuesNeverAuthenticate() {
        RedisService redis = mock(RedisService.class);
        TokenService service = new TokenService(redis, new AuthProperties());
        for (String value : new String[]{"0", "-1", "bad-user-id", "9223372036854775808"}) {
            when(redis.get(anyString())).thenReturn(value);
            BusinessException exception = catchThrowableOfType(BusinessException.class,
                    () -> service.requireUserId("a".repeat(43)));
            assertThat(exception.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }
}
