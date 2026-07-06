package com.elioth.epam.gymcrm.dto.response;

public record CreatedTrainerResponse(
        Long trainerId,
        String username,
        String password
) {}
