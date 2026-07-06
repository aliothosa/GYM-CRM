package com.elioth.epam.gymcrm.dto.request;

public record CreateTrainerRequest(
        String firstName,
        String lastName,
        Long trainingTypeId
) {}
