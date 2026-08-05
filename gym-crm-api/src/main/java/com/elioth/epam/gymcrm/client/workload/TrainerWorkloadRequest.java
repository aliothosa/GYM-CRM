package com.elioth.epam.gymcrm.dto;

import java.time.LocalDate;

public record TrainerWorkloadRequest(
        String trainerUsername,
        String trainerFirstName,
        String trainerLastName,
        Boolean active,
        LocalDate trainingDate,
        Long trainingDurationMinutes,
        String actionType
) {
}