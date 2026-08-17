package com.elioth.epam.gymcrm.messaging;

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
