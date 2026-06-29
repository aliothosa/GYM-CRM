package com.elioth.epam.gymcrm.auth;

public record AuthSession(
        Long id,
        String username,
        Role role
) {}
