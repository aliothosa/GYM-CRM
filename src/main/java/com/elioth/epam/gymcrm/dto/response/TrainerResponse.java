package com.elioth.epam.gymcrm.dto.response;

public record TrainerResponse(
        Long trainerId,
        Long userId,
        String firstName,
        String lastName,
        String username,
        boolean active,
        Long trainingTypeId,
        String trainingTypeName
) {}
