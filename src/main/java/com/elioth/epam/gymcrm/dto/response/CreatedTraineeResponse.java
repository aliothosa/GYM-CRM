package com.elioth.epam.gymcrm.dto.response;

public record CreatedTraineeResponse(
        Long traineeId,
        String username,
        String password
) {}
