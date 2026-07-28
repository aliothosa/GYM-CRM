package com.elioth.epam.gymcrm.security;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/** Tracks failed API logins without persisting credentials or attempts. */
@Service
public class LoginAttemptService {
    private static final int MAX_FAILURES = 3;
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(5);

    private final ConcurrentHashMap<String, LoginAttempt> attempts = new ConcurrentHashMap<>();
    private final Clock clock;

    public LoginAttemptService(Clock clock) {
        this.clock = clock;
    }

    public boolean isBlocked(String username) {
        if (username == null) {
            return false;
        }
        Instant now = clock.instant();
        LoginAttempt attempt = attempts.get(username);
        if (attempt != null && attempt.blockedUntil() != null && !now.isBefore(attempt.blockedUntil())) {
            attempts.remove(username, attempt);
            return false;
        }
        return attempt != null && attempt.blockedUntil() != null;
    }

    /** @return true when this failed attempt creates an active block. */
    public boolean recordFailure(String username) {
        if (username == null) {
            return false;
        }
        Instant now = clock.instant();
        LoginAttempt updated = attempts.compute(username, (key, existing) -> {
            if (existing == null || (existing.blockedUntil() != null && !now.isBefore(existing.blockedUntil()))) {
                return new LoginAttempt(1, null);
            }
            int failures = existing.failures() + 1;
            return failures >= MAX_FAILURES
                    ? new LoginAttempt(failures, now.plus(BLOCK_DURATION))
                    : new LoginAttempt(failures, null);
        });
        return updated.blockedUntil() != null;
    }

    public void reset(String username) {
        if (username != null) {
            attempts.remove(username);
        }
    }
}
