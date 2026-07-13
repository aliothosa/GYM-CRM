package com.elioth.epam.gymcrm.controller;

import com.elioth.epam.gymcrm.auth.AuthSession;
import com.elioth.epam.gymcrm.auth.Role;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.logging.UserLogger;
import com.elioth.epam.gymcrm.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthService authService;
    @Mock
    private UserLogger userLogger;
    @Mock
    private HttpSession httpSession;

    private AuthenticationController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthenticationController(authService, userLogger);
    }

    @Test
    void shouldLoginTrainee() {
        AuthSession session = session("trainee", Role.TRAINEE);
        when(authService.loginTrainee("trainee", "secret")).thenReturn(session);

        ResponseEntity<Void> response = controller.login("trainee", "secret", httpSession);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(httpSession).setAttribute("AUTH_SESSION", session);
        verify(userLogger).log(org.mockito.ArgumentMatchers.eq("trainee"),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.eq("trainee"),
                org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldLoginTrainerWhenTraineeDoesNotExist() {
        AuthSession session = session("trainer", Role.TRAINER);
        when(authService.loginTrainee("trainer", "secret"))
                .thenThrow(new EntityNotFoundException("trainee"));
        when(authService.loginTrainer("trainer", "secret")).thenReturn(session);

        ResponseEntity<Void> response = controller.login("trainer", "secret", httpSession);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(httpSession).setAttribute("AUTH_SESSION", session);
    }

    @Test
    void shouldRejectLoginWhenNeitherRoleExists() {
        when(authService.loginTrainee("missing", "secret"))
                .thenThrow(new EntityNotFoundException("trainee"));
        when(authService.loginTrainer("missing", "secret"))
                .thenThrow(new EntityNotFoundException("trainer"));

        ResponseEntity<Void> response = controller.login("missing", "secret", httpSession);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(httpSession, never()).setAttribute(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void shouldChangeLoginToTrainee() {
        AuthSession previous = session("previous", Role.TRAINER);
        AuthSession replacement = session("replacement", Role.TRAINEE);
        when(authService.loginTrainee("replacement", "secret")).thenReturn(replacement);

        ResponseEntity<Void> response = controller.changeLogin(
                "replacement", "secret", previous, httpSession
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(httpSession).setAttribute("AUTH_SESSION", replacement);
        verify(userLogger).log("replacement", "Successfully authenticated user: {}", "replacement");
        verify(userLogger).log("previous", "logged out of account of user", "previous");
    }

    @Test
    void shouldChangeLoginToTrainerWhenTraineeDoesNotExist() {
        AuthSession previous = session("previous", Role.TRAINEE);
        AuthSession replacement = session("replacement", Role.TRAINER);
        when(authService.loginTrainee("replacement", "secret"))
                .thenThrow(new EntityNotFoundException("trainee"));
        when(authService.loginTrainer("replacement", "secret")).thenReturn(replacement);

        ResponseEntity<Void> response = controller.changeLogin(
                "replacement", "secret", previous, httpSession
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(httpSession).setAttribute("AUTH_SESSION", replacement);
    }

    @Test
    void shouldRejectLoginChangeWhenNeitherRoleExists() {
        AuthSession previous = session("previous", Role.TRAINEE);
        when(authService.loginTrainee("missing", "secret"))
                .thenThrow(new EntityNotFoundException("trainee"));
        when(authService.loginTrainer("missing", "secret"))
                .thenThrow(new EntityNotFoundException("trainer"));

        ResponseEntity<Void> response = controller.changeLogin(
                "missing", "secret", previous, httpSession
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(httpSession, never()).setAttribute(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    private AuthSession session(String username, Role role) {
        return new AuthSession(1L, username, role);
    }
}
