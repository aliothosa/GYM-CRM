package com.elioth.epam.gymcrm.dto.request;

import java.time.LocalDate;

public record CreateTrainingRequest(
        Long traineeId,
        Long trainerId,
        Long trainingTypeId,
        String trainingName,
        LocalDate trainingDate,
        Long duration
) {}
