package com.elioth.epam.gymcrm.security;

import java.time.Instant;

/** In-memory state snapshot for one username's failed login attempts. */
public final class LoginAttempt {
    private final int failures;
    private final Instant blockedUntil;

    public LoginAttempt(int failures, Instant blockedUntil) {
        this.failures = failures;
        this.blockedUntil = blockedUntil;
    }

    public int failures() {
        return failures;
    }

    public Instant blockedUntil() {
        return blockedUntil;
    }
}
