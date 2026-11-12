package com.example.blog.config;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/** 登录令牌配置，固定有效期，不随请求自动续期。 */
@Getter
@Setter
@Validated
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "blog.auth")
public class AuthProperties {

    @Min(value = 1, message = "Token 有效期必须至少为 1 秒")
    private long tokenTtlSeconds = 7200;
}
