package com.elioth.epam.gymcrm.dto.request;

import java.time.LocalDate;

public record GetTraineeTrainingsRestRequest(
    LocalDate periodFrom,
    LocalDate periodTo,
    String trainerName,
    String trainingType
) {
}
