package com.elioth.epam.gymcrm.controller;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@Hidden
@RestController
@RequestMapping("/error")
public class ApiErrorController implements ErrorController {

    @RequestMapping
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Object statusAttribute = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        int statusCode = statusAttribute == null
                ? HttpStatus.INTERNAL_SERVER_ERROR.value()
                : Integer.parseInt(statusAttribute.toString());

        HttpStatus status = HttpStatus.resolve(statusCode);
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        Object messageAttribute = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        String message = messageAttribute == null || messageAttribute.toString().isBlank()
                ? status.getReasonPhrase()
                : messageAttribute.toString();

        Object pathAttribute = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", pathAttribute == null ? request.getRequestURI() : pathAttribute);

        return ResponseEntity.status(status).body(body);
    }
}
