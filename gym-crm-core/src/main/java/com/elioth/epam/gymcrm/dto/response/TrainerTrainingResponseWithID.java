package com.elioth.epam.gymcrm.dto.response;

public record TrainerTrainingResponseWithID(
        Long id,
        TrainerTrainingResponse embeddedResponse
) {
}
