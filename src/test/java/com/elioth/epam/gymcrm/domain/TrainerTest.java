package com.elioth.epam.gymcrm.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainerTest {

    @Test
    void shouldInitializeTraineesCollection() {
        Trainer trainer = new Trainer();

        assertNotNull(trainer.getTrainees());
        assertTrue(trainer.getTrainees().isEmpty());
    }

    @Test
    void shouldAllowSettingUserAndSpecialization() {
        User user = new User();
        user.setUsername("trainer.user");
        TrainingType specialization = new TrainingType();
        specialization.setName("Yoga");
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(specialization);

        assertEquals(user, trainer.getUser());
        assertEquals(specialization, trainer.getSpecialization());
    }
}
