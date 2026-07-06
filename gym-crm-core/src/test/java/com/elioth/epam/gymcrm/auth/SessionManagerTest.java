package com.elioth.epam.gymcrm.auth;

import com.elioth.epam.gymcrm.exception.UserNotAuthenticatedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionManagerTest {

    private SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        sessionManager = new SessionManager();
    }

    @Test
    void shouldStoreSessionOnLogin() {
        AuthSession session = new AuthSession(1L, "Emily.Davis", Role.TRAINEE);

        sessionManager.login(session);

        assertTrue(sessionManager.isAuthenticated());
        assertEquals(session, sessionManager.getCurrentSession());
    }

    @Test
    void shouldClearSessionOnLogout() {
        AuthSession session = new AuthSession(1L, "Emily.Davis", Role.TRAINEE);
        sessionManager.login(session);

        sessionManager.logout();

        assertFalse(sessionManager.isAuthenticated());
    }

    @Test
    void shouldThrowWhenGettingSessionWithoutLogin() {
        UserNotAuthenticatedException exception = assertThrows(
                UserNotAuthenticatedException.class,
                () -> sessionManager.getCurrentSession()
        );

        assertEquals("No user is logged in", exception.getMessage());
    }

    @Test
    void shouldReplaceSessionWhenLoggingInAgain() {
        AuthSession traineeSession = new AuthSession(1L, "Emily.Davis", Role.TRAINEE);
        AuthSession trainerSession = new AuthSession(2L, "John.Smith", Role.TRAINER);

        sessionManager.login(traineeSession);
        sessionManager.login(trainerSession);

        assertTrue(sessionManager.isAuthenticated());
        assertEquals(trainerSession, sessionManager.getCurrentSession());
        assertEquals(Role.TRAINER, sessionManager.getCurrentSession().role());
    }
}
