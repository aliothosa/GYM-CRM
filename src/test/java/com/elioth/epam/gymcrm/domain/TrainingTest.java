package com.elioth.epam.gymcrm.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TrainingTest {

    @Test
    void shouldAllowSettingRequiredFields() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        TrainingType type = new TrainingType();
        type.setName("Cardio");
        LocalDate date = LocalDate.of(2024, 6, 1);
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setType(type);
        training.setName("Morning Run");
        training.setDate(date);
        training.setDurationInMinutes(60L);

        assertEquals(trainee, training.getTrainee());
        assertEquals(trainer, training.getTrainer());
        assertEquals(type, training.getType());
        assertEquals("Morning Run", training.getName());
        assertEquals(date, training.getDate());
        assertEquals(60L, training.getDurationInMinutes());
    }

    @Test
    void shouldStoreTrainingDateAndDuration() {
        Training training = new Training();
        training.setDate(LocalDate.of(2024, 6, 1));
        training.setDurationInMinutes(60L);

        assertEquals(LocalDate.of(2024, 6, 1), training.getDate());
        assertEquals(60L, training.getDurationInMinutes());
    }

    @Test
    void shouldHaveNullTrainingIdBeforePersistence() {
        Training training = new Training();

        assertNull(training.getTrainingId());
    }
}
