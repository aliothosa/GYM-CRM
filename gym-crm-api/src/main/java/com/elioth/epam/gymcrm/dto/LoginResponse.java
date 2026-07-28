package com.elioth.epam.gymcrm.dto;

public record LoginResponse(String accessToken, String tokenType, long expiresIn) {
}
