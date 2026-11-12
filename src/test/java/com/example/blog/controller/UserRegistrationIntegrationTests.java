package com.example.blog.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.blog.entity.Users;
import com.example.blog.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 通过 HTTP、业务层和真实 MyBatis-Plus Mapper 验证注册链路，使用独立的 H2 测试库。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserRegistrationIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void clearUsers() {
        jdbcTemplate.update("DELETE FROM \"BLOG\".\"users\"");
    }

    @Test
    void registersUserWithGeneratedIdentityAndEncryptedPassword() throws Exception {
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"alice","password":"Password123!","nickname":"小爱",
                                 "email":"alice@example.com","avatar":"https://example.com/avatar.png",
                                 "bio":"Hello","id":999,"role":2,"status":0}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("创建成功"))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.username").value("alice"))
                .andExpect(jsonPath("$.data.nickname").value("小爱"))
                .andExpect(jsonPath("$.data.role").value(1))
                .andExpect(jsonPath("$.data.status").value(1))
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.updatedAt").exists())
                .andExpect(jsonPath("$.data.password").doesNotExist());

        Users stored = userMapper.selectOne(Wrappers.<Users>lambdaQuery().eq(Users::getUsername, "alice"));
        assertThat(stored.getId()).isPositive().isNotEqualTo(999L);
        assertThat(stored.getPassword()).isNotEqualTo("Password123!");
        assertThat(passwordEncoder.matches("Password123!", stored.getPassword())).isTrue();
        assertThat(stored.getEmail()).isEqualTo("alice@example.com");
        assertThat(stored.getAvatar()).isEqualTo("https://example.com/avatar.png");
        assertThat(stored.getBio()).isEqualTo("Hello");
        assertThat(stored.getCreatedAt()).isNotNull();
        assertThat(stored.getUpdatedAt()).isNotNull();
        assertThat(stored.getRole()).isEqualTo(1);
        assertThat(stored.getStatus()).isEqualTo(1);
    }

    @Test
    void rejectsDuplicateUsernameWithoutCreatingAnotherUser() throws Exception {
        String request = """
                {"username":"alice","password":"Password123!"}
                """;
        mockMvc.perform(post("/api/user/register").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/user/register").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("用户名已存在"));
        assertThat(userMapper.selectCount(null)).isEqualTo(1);
    }

    @Test
    void rejectsMissingCredentialsWithoutWritingToDatabase() throws Exception {
        mockMvc.perform(post("/api/user/register").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.username").exists())
                .andExpect(jsonPath("$.data.password").exists());
        assertThat(userMapper.selectCount(null)).isZero();
    }

    @Test
    void rejectsInvalidUsernamePasswordAndProfile() throws Exception {
        mockMvc.perform(post("/api/user/register").contentType(MediaType.APPLICATION_JSON).content("""
                        {"username":"has space","password":"short","email":"invalid-email"}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.username").exists())
                .andExpect(jsonPath("$.data.password").exists())
                .andExpect(jsonPath("$.data.email").exists());
        assertThat(userMapper.selectCount(null)).isZero();
    }

    @Test
    void rejectsPasswordExceedingBcryptByteLimit() throws Exception {
        String request = "{\"username\":\"alice\",\"password\":\"" + "密".repeat(25) + "\"}";
        mockMvc.perform(post("/api/user/register").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("密码 UTF-8 编码不能超过 72 字节"));
        assertThat(userMapper.selectCount(null)).isZero();
    }

    @Test
    void rejectsOversizedProfile() throws Exception {
        String request = "{\"username\":\"alice\",\"password\":\"Password123!\",\"bio\":\""
                + "a".repeat(501) + "\"}";
        mockMvc.perform(post("/api/user/register").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.bio").exists());
        assertThat(userMapper.selectCount(null)).isZero();
    }

    @Test
    void wrapsMalformedJsonWithoutWritingToDatabase() throws Exception {
        mockMvc.perform(post("/api/user/register").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isEmpty());
        assertThat(userMapper.selectCount(null)).isZero();
    }
}
