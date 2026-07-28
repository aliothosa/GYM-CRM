package com.elioth.epam.gymcrm.security;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginAttemptServiceTest {
    private final MutableClock clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"));
    private final LoginAttemptService service = new LoginAttemptService(clock);

    @Test
    void firstAndSecondFailuresDoNotBlock() {
        assertFalse(service.recordFailure("user"));
        assertFalse(service.isBlocked("user"));
        assertFalse(service.recordFailure("user"));
        assertFalse(service.isBlocked("user"));
    }

    @Test
    void thirdFailureBlocksForFiveMinutes() {
        service.recordFailure("user");
        service.recordFailure("user");

        assertTrue(service.recordFailure("user"));
        assertTrue(service.isBlocked("user"));
        clock.advanceSeconds(299);
        assertTrue(service.isBlocked("user"));
        clock.advanceSeconds(1);
        assertFalse(service.isBlocked("user"));
    }

    @Test
    void successfulAuthenticationResetAllowsNewFailuresWithoutBlock() {
        service.recordFailure("user");
        service.recordFailure("user");

        service.reset("user");

        assertFalse(service.recordFailure("user"));
        assertFalse(service.isBlocked("user"));
    }

    private static final class MutableClock extends Clock {
        private Instant instant;

        private MutableClock(Instant instant) {
            this.instant = instant;
        }

        void advanceSeconds(long seconds) {
            instant = instant.plusSeconds(seconds);
        }

        @Override
        public ZoneOffset getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(java.time.ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
