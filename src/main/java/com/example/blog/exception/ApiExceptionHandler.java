package com.example.blog.exception;

import com.example.blog.common.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 全局异常处理：业务错误、参数错误和未处理异常统一使用 ApiResponse 返回。
 */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /** 返回业务提示及对应状态，不暴露原始异常原因。 */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException exception, WebRequest request) {
        ApiResponse<Void> response = ApiResponse.error(exception.getStatus().value(), exception.getMessage());
        return handleExceptionInternal(exception, response, new HttpHeaders(), exception.getStatus(), request);
    }

    /** 记录未处理异常，客户端只接收通用提示。 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpectedException(Exception exception, WebRequest request) {
        log.error("处理请求时发生未处理异常", exception);
        ApiResponse<Void> response = ApiResponse.error(500, "服务器内部错误，请稍后重试");
        return handleExceptionInternal(exception, response, new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException exception,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(exception, ApiResponse.error(413, "上传文件或请求体超过大小限制"),
                headers, HttpStatus.PAYLOAD_TOO_LARGE, request);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Object> handleMultipartException(MultipartException exception, WebRequest request) {
        return handleExceptionInternal(exception, ApiResponse.error(400, "上传请求格式错误"),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        ApiResponse<Map<String, String>> response = ApiResponse.error(status.value(), "请求参数校验失败", errors);
        return handleExceptionInternal(exception, response, headers, status, request);
    }

    /** 同步统一 Spring MVC 自带错误响应，并保留 Allow 等协议头和原始 HTTP 状态。 */
    @Override
    protected ResponseEntity<Object> createResponseEntity(Object body, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        Object response = body;
        if (!(body instanceof ApiResponse<?>)) {
            HttpStatus httpStatus = HttpStatus.resolve(status.value());
            String message = httpStatus == null ? "请求处理失败" : httpStatus.getReasonPhrase();
            if (body instanceof ProblemDetail problem && problem.getDetail() != null) {
                message = problem.getDetail();
            }
            response = ApiResponse.error(status.value(), message);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.putAll(headers);
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(response, responseHeaders, status);
    }
}
