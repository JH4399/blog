package com.example.blog.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Getter
@Setter
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "blog.images")
public class ImageStorageProperties {

    /** 应使用持久化目录，多实例部署时应共享此目录。 */
    private Path directory = Path.of("uploads", "images");
}
