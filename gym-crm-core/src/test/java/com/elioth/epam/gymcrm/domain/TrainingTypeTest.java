package com.elioth.epam.gymcrm.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TrainingTypeTest {

    @Test
    void shouldAllowSettingName() {
        TrainingType trainingType = new TrainingType();
        trainingType.setName("Yoga");

        assertEquals("Yoga", trainingType.getName());
    }

    @Test
    void shouldHaveNullIdBeforePersistence() {
        TrainingType trainingType = new TrainingType();

        assertNull(trainingType.getId());
    }
}
