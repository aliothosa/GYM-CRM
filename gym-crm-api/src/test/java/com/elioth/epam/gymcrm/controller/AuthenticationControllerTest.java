package com.elioth.epam.gymcrm.controller;

import com.elioth.epam.gymcrm.dto.LoginRequest;
import com.elioth.epam.gymcrm.security.LoginAttemptService;
import com.elioth.epam.gymcrm.security.TokenRevocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;

/** Replaces the former HttpSession login tests with stateless JWT login coverage. */
@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtEncoder jwtEncoder;
    @Mock private LoginAttemptService loginAttemptService;
    @Mock private TokenRevocationService tokenRevocationService;
    @Mock private Authentication authentication;
    @Mock private org.springframework.security.oauth2.jwt.Jwt jwt;

    private AuthenticationController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthenticationController(authenticationManager, jwtEncoder, loginAttemptService, tokenRevocationService);
    }

    @Test
    void issuesBearerTokenForAuthenticatedUser() {
        when(loginAttemptService.isBlocked("trainee")).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getName()).thenReturn("trainee");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_TRAINEE"))).when(authentication).getAuthorities();
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);
        when(jwt.getTokenValue()).thenReturn("signed-token");

        var response = controller.login(new LoginRequest("trainee", "secret"));

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("signed-token", response.getBody().accessToken());
        assertEquals("Bearer", response.getBody().tokenType());
        verify(loginAttemptService).reset("trainee");
    }

    @Test
    void rejectsInvalidCredentialsAndRecordsFailure() {
        when(loginAttemptService.isBlocked("trainee")).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad credentials"));
        when(loginAttemptService.recordFailure("trainee")).thenReturn(false);

        var response = controller.login(new LoginRequest("trainee", "wrong"));

        assertEquals(401, response.getStatusCode().value());
        verify(loginAttemptService).recordFailure(eq("trainee"));
    }

    @Test
    void rejectsBlockedUserWithoutAttemptingAuthentication() {
        when(loginAttemptService.isBlocked("blocked")).thenReturn(true);

        assertEquals(423, controller.login(new LoginRequest("blocked", "secret")).getStatusCode().value());
    }
}
