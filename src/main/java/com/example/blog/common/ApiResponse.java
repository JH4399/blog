package com.example.blog.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 通用接口返回体，HTTP 状态由 Controller 或全局异常处理器设置。
 *
 * @param <T> 业务数据类型
 */
@Getter
@Schema(description = "统一接口返回结果")
public final class ApiResponse<T> {

    /** 结果状态码，与接口的 HTTP 状态码一致。 */
    @Schema(description = "结果状态码，与 HTTP 状态码一致", example = "200")
    private final int code;

    /** 可向用户展示的提示信息。 */
    @Schema(description = "提示信息", example = "操作成功")
    private final String message;

    /** 业务数据，无数据时为 null；参数错误时可为字段错误映射。 */
    @Schema(description = "业务数据")
    private final T data;

    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /** 操作成功，无返回数据。 */
    public static ApiResponse<Void> success() {
        return success(null);
    }

    /** 操作成功，返回业务数据。 */
    public static <T> ApiResponse<T> success(T data) {
        return success("操作成功", data);
    }

    /** 操作成功，自定义提示并返回数据。 */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    /** 资源创建成功，对应 HTTP 201。 */
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, "创建成功", data);
    }

    /** 操作失败，无附加数据；调用方应同时设置相应 HTTP 状态。 */
    public static ApiResponse<Void> error(int code, String message) {
        return error(code, message, null);
    }

    /** 操作失败，可携带字段校验提示等附加数据。 */
    public static <T> ApiResponse<T> error(int code, String message, T data) {
        if (code < 400 || code > 599) {
            throw new IllegalArgumentException("错误返回必须使用 4xx 或 5xx 状态码");
        }
        return new ApiResponse<>(code, message, data);
    }
}
