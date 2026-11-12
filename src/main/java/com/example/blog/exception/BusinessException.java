package com.example.blog.exception;

import org.springframework.http.HttpStatus;

import java.util.Objects;

/**
 * 可向客户端展示的业务异常，由 {@link ApiExceptionHandler} 统一转换为错误响应。
 * message 只应包含业务提示，不应包含 SQL、密码或内部异常详情。
 *
 * <pre>{@code
 * throw BusinessException.conflict("用户名已存在");
 * throw BusinessException.notFound("文章不存在");
 * }</pre>
 */
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    /** 默认使用 400 状态返回业务提示。 */
    public BusinessException(String message) {
        this(HttpStatus.BAD_REQUEST, message);
    }

    /** 指定错误状态和可公开的业务提示。 */
    public BusinessException(HttpStatus status, String message) {
        this(status, message, null);
    }

    /** 保留原始异常作为原因，响应中仅展示 message。 */
    public BusinessException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = Objects.requireNonNull(status, "错误状态不能为空");
        if (!status.isError()) {
            throw new IllegalArgumentException("业务异常必须使用 4xx 或 5xx 状态");
        }
    }

    public HttpStatus getStatus() {
        return status;
    }

    /** 400：请求参数或业务操作不合法。 */
    public static BusinessException badRequest(String message) {
        return new BusinessException(HttpStatus.BAD_REQUEST, message);
    }

    /** 401：尚未登录或身份验证失败。 */
    public static BusinessException unauthorized(String message) {
        return new BusinessException(HttpStatus.UNAUTHORIZED, message);
    }

    /** 403：当前用户无权执行操作。 */
    public static BusinessException forbidden(String message) {
        return new BusinessException(HttpStatus.FORBIDDEN, message);
    }

    /** 404：目标资源不存在。 */
    public static BusinessException notFound(String message) {
        return new BusinessException(HttpStatus.NOT_FOUND, message);
    }

    /** 409：资源重复或当前状态冲突。 */
    public static BusinessException conflict(String message) {
        return new BusinessException(HttpStatus.CONFLICT, message);
    }
}
