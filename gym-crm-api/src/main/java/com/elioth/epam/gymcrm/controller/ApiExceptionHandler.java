package com.elioth.epam.gymcrm.controller;

import com.elioth.epam.gymcrm.dto.ApiErrorResponse;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.exception.InvalidEntityException;
import com.elioth.epam.gymcrm.exception.InvalidRequestException;
import com.elioth.epam.gymcrm.logging.TransactionIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class,
            HttpMessageNotReadableException.class, InvalidRequestException.class, InvalidEntityException.class})
    ResponseEntity<ApiErrorResponse> badRequest(Exception exception, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "Invalid request", request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    ResponseEntity<ApiErrorResponse> notFound(EntityNotFoundException exception, HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, "Requested resource was not found", request);
    }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ApiErrorResponse> unauthorized(AuthenticationException exception, HttpServletRequest request) {
        return response(HttpStatus.UNAUTHORIZED, "Authentication is required", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiErrorResponse> forbidden(AccessDeniedException exception, HttpServletRequest request) {
        return response(HttpStatus.FORBIDDEN, "Access is denied", request);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponse> unexpected(Exception exception, HttpServletRequest request) {
        LOG.error("operation=API_ERROR path={} exceptionType={}", request.getRequestURI(),
                exception.getClass().getSimpleName());
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected internal server error", request);
    }

    private ResponseEntity<ApiErrorResponse> response(HttpStatus status, String message, HttpServletRequest request) {
        ApiErrorResponse body = new ApiErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(),
                message, request.getRequestURI(), MDC.get(TransactionIdFilter.MDC_KEY));
        return ResponseEntity.status(status).body(body);
    }
}
