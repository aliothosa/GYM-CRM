package com.elioth.epam.gymcrm.dto.response;

import java.time.LocalDate;

public record TrainingResponse(
        Long trainingId,
        Long traineeId,
        String traineeUsername,
        String traineeFirstName,
        String traineeLastName,
        Long trainerId,
        String trainerUsername,
        String trainerFirstName,
        String trainerLastName,
        Long trainingTypeId,
        String trainingTypeName,
        String trainingName,
        LocalDate trainingDate,
        Long duration
) {}
