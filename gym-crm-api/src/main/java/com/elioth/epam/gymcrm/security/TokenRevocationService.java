package com.elioth.epam.gymcrm.security;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/** Holds revoked JWT ids until their normal expiration time. */
@Service
public class TokenRevocationService {
    private final ConcurrentHashMap<String, Instant> revokedTokens = new ConcurrentHashMap<>();
    private final Clock clock;

    public TokenRevocationService(Clock clock) {
        this.clock = clock;
    }

    public void revoke(String tokenId, Instant expiresAt) {
        removeExpired();
        if (tokenId != null && expiresAt != null && clock.instant().isBefore(expiresAt)) {
            revokedTokens.put(tokenId, expiresAt);
        }
    }

    public boolean isRevoked(String tokenId) {
        removeExpired();
        return tokenId != null && revokedTokens.containsKey(tokenId);
    }

    private void removeExpired() {
        Instant now = clock.instant();
        revokedTokens.entrySet().removeIf(entry -> !now.isBefore(entry.getValue()));
    }
}
