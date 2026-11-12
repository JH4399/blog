package com.example.blog.exception;

import com.example.blog.common.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ApiExceptionHandlerTests {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ErrorController())
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void returnsBusinessStatusAndMessageWithoutExposingCause() throws Exception {
        mockMvc.perform(get("/test/business"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("文章不存在"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.cause").doesNotExist())
                .andExpect(jsonPath("$.stackTrace").doesNotExist());
    }

    @Test
    void hidesUnexpectedExceptionDetails() throws Exception {
        mockMvc.perform(get("/test/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("服务器内部错误，请稍后重试"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.stackTrace").doesNotExist());
    }

    @Test
    void keepsFrameworkParameterErrorsAsBadRequests() throws Exception {
        mockMvc.perform(get("/test/parameter").param("id", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void keepsExistingResponseStatusExceptionStatus() throws Exception {
        mockMvc.perform(get("/test/http-status"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("无权访问"));
    }

    @Test
    void wrapsSuccessfulDataWithCustomMessage() throws Exception {
        mockMvc.perform(get("/test/success"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data").value("hello"));
    }

    @Test
    void returnsSuccessfulResponseWithoutData() throws Exception {
        mockMvc.perform(get("/test/empty"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"code":200,"message":"操作成功","data":null}
                        """));
    }

    @Test
    void keepsMethodNotAllowedStatusAndProtocolHeader() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/test/success"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string("Allow", "GET"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(405));
    }

    @RestController
    static class ErrorController {

        @GetMapping("/test/success")
        ApiResponse<String> success() {
            return ApiResponse.success("查询成功", "hello");
        }

        @GetMapping("/test/empty")
        ApiResponse<Void> empty() {
            return ApiResponse.success();
        }

        @GetMapping("/test/business")
        String business() {
            throw new BusinessException(HttpStatus.NOT_FOUND, "文章不存在",
                    new IllegalStateException("internal database details"));
        }

        @GetMapping("/test/unexpected")
        String unexpected() {
            throw new IllegalStateException("internal database details");
        }

        @GetMapping("/test/parameter")
        String parameter(@RequestParam("id") Long id) {
            return id.toString();
        }

        @GetMapping("/test/http-status")
        String httpStatus() {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问");
        }
    }
}
