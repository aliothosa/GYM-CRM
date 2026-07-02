package com.elioth.epam.gymcrm.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthSessionTest {

    @Test
    void shouldExposeIdUsernameAndRole() {
        AuthSession session = new AuthSession(1L, "Emily.Davis", Role.TRAINEE);

        assertEquals(1L, session.id());
        assertEquals("Emily.Davis", session.username());
        assertEquals(Role.TRAINEE, session.role());
    }
}
