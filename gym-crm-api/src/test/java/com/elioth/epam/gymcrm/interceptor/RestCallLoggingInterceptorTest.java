package com.elioth.epam.gymcrm.interceptor;

import com.elioth.epam.gymcrm.auth.AuthSession;
import com.elioth.epam.gymcrm.auth.Role;
import com.elioth.epam.gymcrm.logging.UserLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class RestCallLoggingInterceptorTest {

    @Mock
    private UserLogger userLogger;

    private RestCallLoggingInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new RestCallLoggingInterceptor(userLogger);
    }

    @Test
    void shouldLogAuthenticatedRequestWithOkResponse() {
        MockHttpServletRequest request = request("PUT", "/trainees/user1");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("AUTH_SESSION", new AuthSession(1L, "user1", Role.TRAINEE));
        request.setSession(session);
        request.addParameter("status", "true");
        MockHttpServletResponse response = response(HttpStatus.OK);

        complete(request, response, null);

        assertEquals(
                "REST call: method=PUT, endpoint=/trainees/user1, request={status=true}, response=200 OK",
                loggedMessage("user1")
        );
    }

    @Test
    void shouldUseUsernameParameterWhenSessionIsMissing() {
        MockHttpServletRequest request = request("POST", "/auth/login");
        request.addParameter("username", "login.user");
        MockHttpServletResponse response = response(HttpStatus.OK);

        complete(request, response, null);

        assertTrue(loggedMessage("login.user").contains("request={username=login.user}"));
    }

    @Test
    void shouldUseAnonymousWhenUsernameIsUnavailable() {
        MockHttpServletRequest request = request("GET", "/training-types");
        MockHttpServletResponse response = response(HttpStatus.OK);

        complete(request, response, null);

        assertEquals(
                "REST call: method=GET, endpoint=/training-types, request={}, response=200 OK",
                loggedMessage("anonymous")
        );
        verifyNoMoreInteractions(userLogger);
    }

    @Test
    void shouldUseAnonymousForBlankUsernameAndNonAuthenticationSessionAttribute() {
        MockHttpServletRequest request = request("GET", "/trainees");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("AUTH_SESSION", "not-an-auth-session");
        request.setSession(session);
        request.addParameter("username", "   ");

        complete(request, response(HttpStatus.OK), null);

        assertTrue(loggedMessage("anonymous").contains("username=   "));
    }

    @Test
    void shouldLogErrorResponse() {
        MockHttpServletRequest request = request("GET", "/trainees/unknown");
        MockHttpServletResponse response = response(HttpStatus.NOT_FOUND);

        complete(request, response, null);

        assertTrue(loggedMessage("anonymous").endsWith("response=404 Not Found"));
    }

    @Test
    void shouldNotLogPasswordParameters() {
        MockHttpServletRequest request = request("POST", "/auth/login");
        request.addParameter("username", "safe.user");
        request.addParameter("password", "secret1");
        request.addParameter("oldPassword", "secret2");
        request.addParameter("NEWPASSWORD", "secret3");
        MockHttpServletResponse response = response(HttpStatus.UNAUTHORIZED);

        complete(request, response, null);

        String message = loggedMessage("safe.user");
        assertFalse(message.toLowerCase().contains("password"));
        assertFalse(message.contains("secret1"));
        assertFalse(message.contains("secret2"));
        assertFalse(message.contains("secret3"));
    }

    @Test
    void shouldIncludeExceptionMessage() {
        MockHttpServletRequest request = request("GET", "/trainees/unknown");
        MockHttpServletResponse response = response(HttpStatus.NOT_FOUND);

        complete(request, response, new IllegalArgumentException("Entity not found"));

        assertTrue(loggedMessage("anonymous").endsWith(
                "response=404 Not Found, message=Entity not found"
        ));
    }

    @Test
    void shouldHandleUnknownStatusAndExceptionWithoutMessage() {
        MockHttpServletRequest request = request("GET", "/custom");
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(599);

        complete(request, response, new RuntimeException());

        assertTrue(loggedMessage("anonymous").endsWith("response=599 Unknown"));
    }

    @Test
    void shouldLogMultipleSafeParameterValues() {
        MockHttpServletRequest request = request("GET", "/trainers");
        request.addParameter("specialty", "Yoga", "Fitness");

        complete(request, response(HttpStatus.OK), null);

        assertTrue(loggedMessage("anonymous").contains("specialty=[Yoga, Fitness]"));
    }

    @Test
    void shouldUseUserLoggerExactlyOnceForCompletedRequest() {
        MockHttpServletRequest request = request("GET", "/trainees/user1");
        MockHttpServletResponse response = response(HttpStatus.FORBIDDEN);

        complete(request, response, null);

        verify(userLogger, times(1)).log(eq("anonymous"), org.mockito.ArgumentMatchers.anyString());
        verifyNoMoreInteractions(userLogger);
    }

    private MockHttpServletRequest request(String method, String uri) {
        return new MockHttpServletRequest(method, uri);
    }

    private MockHttpServletResponse response(HttpStatus status) {
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(status.value());
        return response;
    }

    private void complete(
            MockHttpServletRequest request,
            MockHttpServletResponse response,
            Exception exception
    ) {
        interceptor.afterCompletion(request, response, new Object(), exception);
    }

    private String loggedMessage(String username) {
        ArgumentCaptor<String> message = ArgumentCaptor.forClass(String.class);
        verify(userLogger).log(eq(username), message.capture());
        return message.getValue();
    }
}
