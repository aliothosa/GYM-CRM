package com.elioth.epam.gymcrm.controller;

import jakarta.servlet.RequestDispatcher;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ApiErrorControllerTest {

    private final ApiErrorController controller = new ApiErrorController();

    @Test
    void shouldReturnErrorStatusAndSafeJsonBody() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 404);
        request.setAttribute(RequestDispatcher.ERROR_MESSAGE, "Resource not found");
        request.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, "/missing");
        request.setAttribute(RequestDispatcher.ERROR_EXCEPTION, new IllegalStateException("internal"));

        ResponseEntity<Map<String, Object>> response = controller.handleError(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("Not Found", response.getBody().get("error"));
        assertEquals("Not Found", response.getBody().get("message"));
        assertEquals("/missing", response.getBody().get("path"));
        assertFalse(response.getBody().containsKey("trace"));
        assertFalse(response.getBody().containsKey("exception"));
    }

    @Test
    void shouldUseInternalServerErrorWhenStatusIsMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/error");

        ResponseEntity<Map<String, Object>> response = controller.handleError(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().get("status"));
        assertEquals("Internal Server Error", response.getBody().get("error"));
        assertEquals("Internal Server Error", response.getBody().get("message"));
        assertEquals("/error", response.getBody().get("path"));
        assertFalse(response.getBody().containsKey("trace"));
    }

    @Test
    void shouldUseInternalServerErrorForUnknownStatusAndBlankMessage() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 799);
        request.setAttribute(RequestDispatcher.ERROR_MESSAGE, "   ");
        request.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, "/custom-error");

        ResponseEntity<Map<String, Object>> response = controller.handleError(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().get("status"));
        assertEquals("Internal Server Error", response.getBody().get("message"));
        assertEquals("/custom-error", response.getBody().get("path"));
    }
}
