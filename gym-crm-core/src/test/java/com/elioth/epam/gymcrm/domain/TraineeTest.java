package com.elioth.epam.gymcrm.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TraineeTest {

    @Test
    void shouldInitializeTrainersAndTrainingsCollections() {
        Trainee trainee = new Trainee();

        assertNotNull(trainee.getTrainers());
        assertNotNull(trainee.getTrainings());
        assertTrue(trainee.getTrainers().isEmpty());
        assertTrue(trainee.getTrainings().isEmpty());
    }

    @Test
    void shouldAllowSettingUserAndAddress() {
        User user = new User();
        user.setUsername("trainee.user");
        Address address = new Address("Main St", "Boston", "MA", "02101", 42);
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setAddress(address);
        trainee.setBirthDate(LocalDate.of(1990, 5, 15));

        assertEquals(user, trainee.getUser());
        assertEquals(address, trainee.getAddress());
        assertEquals(LocalDate.of(1990, 5, 15), trainee.getBirthDate());
    }
}
