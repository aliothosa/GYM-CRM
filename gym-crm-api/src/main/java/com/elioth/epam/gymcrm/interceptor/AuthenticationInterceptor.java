package com.elioth.epam.gymcrm.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {


    private static final String AUTH_SESSION = "AUTH_SESSION";

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(AUTH_SESSION) == null) {
            response.sendError(HttpStatus.FORBIDDEN.value());
            return false;
        }

        return true;
    }
}
