package com.elioth.epam.gymcrm.interceptor;

import com.elioth.epam.gymcrm.auth.AuthSession;
import com.elioth.epam.gymcrm.auth.Role;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthenticationInterceptorTest {

    private final AuthenticationInterceptor interceptor = new AuthenticationInterceptor();

    @Test
    void shouldRejectRequestWithoutSession() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(
                new MockHttpServletRequest(), response, new Object()
        );

        assertFalse(allowed);
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    void shouldRejectRequestWithoutAuthenticationAttribute() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(new MockHttpSession());
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse(interceptor.preHandle(request, response, new Object()));
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    void shouldAllowAuthenticatedRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("AUTH_SESSION", new AuthSession(1L, "user", Role.TRAINEE));
        request.setSession(session);

        assertTrue(interceptor.preHandle(
                request, new MockHttpServletResponse(), new Object()
        ));
    }
}
