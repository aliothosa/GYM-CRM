package com.elioth.epam.gymcrm.auth;

import com.elioth.epam.gymcrm.exception.UserNotAuthenticatedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("Practice skeleton - remove @Disabled when implementing tests")
class SessionManagerTest {

    private SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        sessionManager = new SessionManager();
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldStoreSessionOnLogin() {
        // Arrange
        // TODO: AuthSession session = new AuthSession(1L, "Emily.Davis", Role.TRAINEE)

        // Act
        // TODO: sessionManager.login(session)

        // Assert
        // TODO: assertTrue(sessionManager.isAuthenticated())
        // TODO: assertEquals(session, sessionManager.getCurrentSession())
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldClearSessionOnLogout() {
        // Arrange
        // TODO: login a session first

        // Act
        // TODO: sessionManager.logout()

        // Assert
        // TODO: assertFalse(sessionManager.isAuthenticated())
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowWhenGettingSessionWithoutLogin() {
        // Act & Assert
        // TODO: assertThrows(UserNotAuthenticatedException.class, () -> sessionManager.getCurrentSession())
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldReplaceSessionWhenLoggingInAgain() {
        // Arrange
        // TODO: login trainee session, then login trainer session

        // Assert
        // TODO: assert current session is the latest one (trainer)
    }
}
