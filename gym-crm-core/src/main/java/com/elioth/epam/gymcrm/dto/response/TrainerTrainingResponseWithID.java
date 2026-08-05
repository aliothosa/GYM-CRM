package com.elioth.epam.gymcrm.dto.response;

public record TrainerResponseWithID(
        Long id,
        TrainerTrainingResponse embeddedResponse
) {
}
