package com.elioth.epam.gymcrm.event;

import java.time.LocalDate;

public record TrainingWorkloadChangedEvent(
        String trainerUsername,
        String trainerFirstName,
        String trainerLastName,
        boolean trainerActive,
        LocalDate trainingDate,
        long trainingDurationMinutes,
        WorkloadAction action
) {
}