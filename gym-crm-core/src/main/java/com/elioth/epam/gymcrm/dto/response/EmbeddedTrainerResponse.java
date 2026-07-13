package com.elioth.epam.gymcrm.dto.response;

public record EmbeddedTrainerResponse(
        String username,
        String firstName,
        String lastName,
        String Specialization
) {
}
