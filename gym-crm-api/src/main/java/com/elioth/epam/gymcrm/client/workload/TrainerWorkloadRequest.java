package com.elioth.epam.gymcrm.client.workload;

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