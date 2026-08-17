package com.elioth.epam.workload.messaging;

import java.time.LocalDate;

public record TrainerWorkloadMessage(
        String trainerUsername,
        String trainerFirstName,
        String trainerLastName,
        Boolean trainerActive,
        LocalDate trainingDate,
        long trainingDurationMinutes,
        String action
) {
}
