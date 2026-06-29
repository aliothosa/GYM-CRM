package com.elioth.epam.gymcrm.domain;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Optional/low-priority domain test; enable when practicing entity field assignment.")
class TrainingTest {

    @Test
    void shouldAllowSettingRequiredFields() {
        // TODO: create Trainee, Trainer, TrainingType, and Training
        // TODO: set trainee, trainer, type, name, date, durationInMinutes
        // TODO: assert all getters return the assigned values
    }

    @Test
    void shouldStoreTrainingDateAndDuration() {
        // TODO: create Training with date LocalDate.of(2024, 6, 1) and durationInMinutes 60L
        // TODO: assert getDate() and getDurationInMinutes() match persisted values
    }
}
