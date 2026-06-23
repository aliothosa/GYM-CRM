package com.elioth.epam.gymcrm.dto.request;

import java.time.LocalDate;

public record CreateTrainingRequest(
        long traineeId,
        long trainerId,
        long trainingTypeId,
        String trainingName,
        LocalDate trainingDate,
        long duration
){}
