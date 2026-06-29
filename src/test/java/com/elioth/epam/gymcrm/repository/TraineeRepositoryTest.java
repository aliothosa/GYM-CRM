package com.elioth.epam.gymcrm.repository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
@Disabled("Requires H2 or dedicated test database configuration; enable after test profile is set up.")
class TraineeRepositoryTest {

    @Autowired
    private TraineeRepository traineeRepository;

    @Test
    void findByUserUsername_shouldReturnTrainee_whenUsernameExists() {
        // TODO: persist User + Trainee with username "john.doe"
        // TODO: call traineeRepository.findByUserUsername("john.doe")
        // TODO: assert Optional.isPresent(), trainee.user.username equals "john.doe"
    }

    @Test
    void findByUserUsername_shouldReturnEmpty_whenUsernameNotFound() {
        // TODO: persist no trainees (or only unrelated usernames)
        // TODO: call traineeRepository.findByUserUsername("missing.user")
        // TODO: assert Optional.isEmpty()
    }

    @Test
    void findByUserUsernameAndUserPassword_shouldReturnTrainee_whenCredentialsMatch() {
        // TODO: persist User (username "john.doe", password "secret") + linked Trainee
        // TODO: call traineeRepository.findByUserUsernameAndUserPassword("john.doe", "secret")
        // TODO: assert Optional.isPresent(), returned trainee matches persisted entity
    }

    @Test
    void findByUserUsernameAndUserPassword_shouldReturnEmpty_whenPasswordMismatch() {
        // TODO: persist User + Trainee with username "john.doe" and password "secret"
        // TODO: call traineeRepository.findByUserUsernameAndUserPassword("john.doe", "wrong")
        // TODO: assert Optional.isEmpty()
    }
}
