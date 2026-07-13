package com.elioth.epam.gymcrm.interceptor;

import com.elioth.epam.gymcrm.auth.AuthSession;
import com.elioth.epam.gymcrm.logging.UserLogger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

@Component
public class RestCallLoggingInterceptor implements HandlerInterceptor {

    private static final String AUTH_SESSION = "AUTH_SESSION";
    private static final String ANONYMOUS = "anonymous";

    private final UserLogger userLogger;

    public RestCallLoggingInterceptor(UserLogger userLogger) {
        this.userLogger = userLogger;
    }

    @Override
    public void afterCompletion(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            @Nullable Exception exception
    ) {
        String username = resolveUsername(request);
        String message = buildMessage(request, response, exception);
        userLogger.log(username, message);
    }

    private String resolveUsername(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(AUTH_SESSION) instanceof AuthSession authSession) {
            return authSession.username();
        }

        String username = request.getParameter("username");
        return username == null || username.isBlank() ? ANONYMOUS : username;
    }

    private String buildMessage(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception
    ) {
        int statusCode = response.getStatus();
        HttpStatus status = HttpStatus.resolve(statusCode);
        String statusDescription = status == null ? "Unknown" : status.getReasonPhrase();

        String message = "REST call: method=" + request.getMethod()
                + ", endpoint=" + request.getRequestURI()
                + ", request=" + safeParameters(request)
                + ", response=" + statusCode + " " + statusDescription;

        if (exception != null && exception.getMessage() != null) {
            message += ", message=" + exception.getMessage();
        }

        return message;
    }

    private Map<String, Object> safeParameters(HttpServletRequest request) {
        Map<String, Object> parameters = new TreeMap<>();

        request.getParameterMap().forEach((name, values) -> {
            if (!name.toLowerCase(Locale.ROOT).contains("password")) {
                Object value = values.length == 1 ? values[0] : Arrays.asList(values);
                parameters.put(name, value);
            }
        });

        return parameters;
    }
}
