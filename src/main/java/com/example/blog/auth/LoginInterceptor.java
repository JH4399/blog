package com.example.blog.auth;

import com.example.blog.service.UserService;
import com.example.blog.vo.UsersVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/** 登录态仅保存在本次请求属性中，不使用 ThreadLocal，也不创建 HttpSession。 */
@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    public static final String CURRENT_USER = "blog.currentUser";
    public static final String CURRENT_TOKEN = "blog.currentToken";

    private final TokenManager tokenManager;
    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }
        boolean requiresLogin = AnnotatedElementUtils.hasAnnotation(method.getMethod(), LoginRequired.class)
                || AnnotatedElementUtils.hasAnnotation(method.getBeanType(), LoginRequired.class);
        if (!requiresLogin) {
            return true;
        }
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        String token = tokenManager.resolveBearerToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        UsersVO user = userService.currentUser(token);
        request.setAttribute(CURRENT_USER, user);
        request.setAttribute(CURRENT_TOKEN, token);
        return true;
    }
}
