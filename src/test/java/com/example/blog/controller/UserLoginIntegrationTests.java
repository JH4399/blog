package com.example.blog.controller;

import com.example.blog.common.RedisService;
import com.example.blog.entity.Users;
import com.example.blog.mapper.UserMapper;
import com.example.blog.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HTTP、真实业务层、BCrypt、MyBatis-Plus 与 H2 的链路测试。
 * 只替换 Redis 边界，默认 mvn test 无需本地 Redis，且不访问业务数据库。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserLoginIntegrationTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserMapper userMapper;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private RedisService redisService;

    private final Map<String, String> sessions = new HashMap<>();
    private final Map<String, Duration> ttls = new HashMap<>();

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM \"BLOG\".\"users\"");
        sessions.clear();
        ttls.clear();
        when(redisService.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            if (sessions.containsKey(key)) {
                return false;
            }
            sessions.put(key, invocation.getArgument(1));
            ttls.put(key, invocation.getArgument(2));
            return true;
        });
        when(redisService.get(anyString())).thenAnswer(invocation -> sessions.get(invocation.getArgument(0)));
        doAnswer(invocation -> sessions.remove(invocation.getArgument(0)) != null)
                .when(redisService).delete(anyString());
    }

    @Test
    void registersLogsInReadsCurrentUserAndLogsOut() throws Exception {
        mockMvc.perform(post("/api/user/register").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"alice","password":"Password123!","nickname":"小爱"}
                                """))
                .andExpect(status().isCreated());
        String token = login("alice", "Password123!");
        assertThat(token).matches("[A-Za-z0-9_-]{43}");
        String key = tokenKey(token);
        assertThat(sessions).containsOnlyKeys(key);
        assertThat(sessions.get(key)).matches("[0-9]+").doesNotContain(token);
        assertThat(ttls.get(key)).isEqualTo(Duration.ofHours(2));

        mockMvc.perform(get("/api/user/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "no-store"))
                .andExpect(jsonPath("$.data.username").value("alice"))
                .andExpect(jsonPath("$.data.nickname").value("小爱"))
                .andExpect(jsonPath("$.data.password").doesNotExist());
        mockMvc.perform(post("/api/user/logout").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("退出登录成功"));
        assertThat(sessions).isEmpty();
        mockMvc.perform(get("/api/user/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void hidesWhetherUsernameExistsAndDoesNotCreateSessionsForWrongCredentials() throws Exception {
        createUser("alice", "Password123!", 1);
        for (String username : new String[]{"alice", "missing"}) {
            mockMvc.perform(post("/api/user/login").contentType(MediaType.APPLICATION_JSON)
                            .content(credentials(username, "wrong-password")))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("用户名或密码错误"))
                    .andExpect(jsonPath("$.data").isEmpty());
        }
        verifyNoInteractions(redisService);
    }

    @Test
    void rejectsDisabledUserOnlyAfterCheckingPassword() throws Exception {
        createUser("alice", "Password123!", 0);
        mockMvc.perform(post("/api/user/login").contentType(MediaType.APPLICATION_JSON)
                        .content(credentials("alice", "wrong-password")))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/user/login").contentType(MediaType.APPLICATION_JSON)
                        .content(credentials("alice", "Password123!")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("账号已被禁用"));
        verifyNoInteractions(redisService);
    }

    @Test
    void rejectsInvalidParametersAndPasswordsOverBcryptByteLimit() throws Exception {
        for (String body : new String[]{"{}", "{", credentials("has space", "pass"),
                credentials("a".repeat(51), "pass"), credentials("alice", "a".repeat(73)),
                credentials("alice", "密".repeat(25))}) {
            mockMvc.perform(post("/api/user/login").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));
        }
        verifyNoInteractions(redisService);
    }

    @Test
    void acceptsExactly72Utf8BytesWithoutTrimmingPassword() throws Exception {
        String password = "密".repeat(23) + " a ";
        createUser("alice", password, 1);
        login("alice", password);
        mockMvc.perform(post("/api/user/login").contentType(MediaType.APPLICATION_JSON)
                        .content(credentials("alice", password.trim())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsMissingMalformedUnknownAndExpiredTokens() throws Exception {
        mockMvc.perform(get("/api/user/me")).andExpect(status().isUnauthorized());
        for (String authorization : new String[]{"Basic secret", "Bearer ", "Bearer bad", "Bearer " + "a".repeat(43)}) {
            mockMvc.perform(get("/api/user/me").header(HttpHeaders.AUTHORIZATION, authorization))
                    .andExpect(status().isUnauthorized());
        }
        createUser("alice", "Password123!", 1);
        String token = login("alice", "Password123!");
        // Redis 过期后 GET 返回 null；HTTP 层不得接受已经消失的登录态。
        sessions.remove(tokenKey(token));
        mockMvc.perform(get("/api/user/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void readsLatestRoleAndRejectsAccountDisabledAfterLogin() throws Exception {
        Users user = createUser("alice", "Password123!", 1);
        String token = login("alice", "Password123!");
        user.setRole(2);
        userMapper.updateById(user);
        mockMvc.perform(get("/api/user/me").header(HttpHeaders.AUTHORIZATION, "bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.role").value(2));
        user.setStatus(0);
        userMapper.updateById(user);
        mockMvc.perform(get("/api/user/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
        assertThat(sessions).isEmpty();
    }

    @Test
    void rejectsDeletedAccountsAndRemovesTheirSession() throws Exception {
        Users user = createUser("alice", "Password123!", 1);
        String token = login("alice", "Password123!");
        userMapper.deleteById(user.getId());
        mockMvc.perform(get("/api/user/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isUnauthorized());
        assertThat(sessions).isEmpty();
    }

    @Test
    void logoutDoesNotRevokeAnotherDevice() throws Exception {
        createUser("alice", "Password123!", 1);
        String first = login("alice", "Password123!");
        String second = login("alice", "Password123!");
        assertThat(first).isNotEqualTo(second);
        mockMvc.perform(post("/api/user/logout").header(HttpHeaders.AUTHORIZATION, "Bearer " + first))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/user/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + second))
                .andExpect(status().isOk());
    }

    @Test
    void redisFailureFailsLoginAuthenticationAndLogoutWith503() throws Exception {
        createUser("alice", "Password123!", 1);
        String token = login("alice", "Password123!");
        when(redisService.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .thenThrow(new RedisConnectionFailureException("internal redis address"));
        mockMvc.perform(post("/api/user/login").contentType(MediaType.APPLICATION_JSON)
                        .content(credentials("alice", "Password123!")))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value(503))
                .andExpect(jsonPath("$.data").isEmpty());
        when(redisService.delete(anyString())).thenThrow(new RedisConnectionFailureException("internal address"));
        mockMvc.perform(post("/api/user/logout").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isServiceUnavailable());
        when(redisService.get(anyString())).thenThrow(new RedisConnectionFailureException("internal address"));
        mockMvc.perform(get("/api/user/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("登录服务暂不可用，请稍后重试"));
    }

    private String login(String username, String password) throws Exception {
        String response = mockMvc.perform(post("/api/user/login").contentType(MediaType.APPLICATION_JSON)
                        .content(credentials(username, password)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "no-store"))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").value(7200))
                .andExpect(jsonPath("$.data.user.password").doesNotExist())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readTree(response).path("data").path("accessToken").asText();
    }

    private String credentials(String username, String password) {
        return objectMapper.writeValueAsString(Map.of("username", username, "password", password));
    }

    private Users createUser(String username, String password, int status) {
        Users user = new Users();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(status);
        user.setRole(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
        return user;
    }

    private static String tokenKey(String token) throws Exception {
        return TokenService.KEY_PREFIX + HexFormat.of().formatHex(
                MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8)));
    }
}
