package com.elioth.epam.gymcrm.repository;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class TraineeRepositoryTest {

    @Autowired
    private TraineeRepository traineeRepository;

    private Trainee persistTrainee(String username, String password) {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        return traineeRepository.save(trainee);
    }

    @Test
    void findByUserUsername_shouldReturnTrainee_whenUsernameExists() {
        persistTrainee("john.doe", "secret");

        Optional<Trainee> result = traineeRepository.findByUserUsername("john.doe");

        assertTrue(result.isPresent());
        assertEquals("john.doe", result.get().getUser().getUsername());
    }

    @Test
    void findByUserUsername_shouldReturnEmpty_whenUsernameNotFound() {
        persistTrainee("john.doe", "secret");

        Optional<Trainee> result = traineeRepository.findByUserUsername("missing.user");

        assertTrue(result.isEmpty());
    }

    @Test
    void findByUserUsernameAndUserPassword_shouldReturnTrainee_whenCredentialsMatch() {
        Trainee persisted = persistTrainee("john.doe", "secret");

        Optional<Trainee> result = traineeRepository.findByUserUsernameAndUserPassword("john.doe", "secret");

        assertTrue(result.isPresent());
        assertEquals(persisted.getTraineeId(), result.get().getTraineeId());
    }

    @Test
    void findByUserUsernameAndUserPassword_shouldReturnEmpty_whenPasswordMismatch() {
        persistTrainee("john.doe", "secret");

        Optional<Trainee> result = traineeRepository.findByUserUsernameAndUserPassword("john.doe", "wrong");

        assertTrue(result.isEmpty());
    }
}
