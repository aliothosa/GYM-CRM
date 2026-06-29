package com.elioth.epam.gymcrm.repository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
@Disabled("Requires H2 or dedicated test database configuration; enable after test profile is set up.")
class TrainerRepositoryTest {

    @Autowired
    private TrainerRepository trainerRepository;

    @Test
    void findByUserUsername_shouldReturnTrainer_whenUsernameExists() {
        // TODO: persist User + Trainer with username "jane.trainer"
        // TODO: call trainerRepository.findByUserUsername("jane.trainer")
        // TODO: assert Optional.isPresent(), trainer.user.username equals "jane.trainer"
    }

    @Test
    void findByUserUsername_shouldReturnEmpty_whenUsernameNotFound() {
        // TODO: persist no trainers (or only unrelated usernames)
        // TODO: call trainerRepository.findByUserUsername("missing.trainer")
        // TODO: assert Optional.isEmpty()
    }

    @Test
    void findByUserUsernameAndUserPassword_shouldReturnTrainer_whenCredentialsMatch() {
        // TODO: persist User (username "jane.trainer", password "secret") + linked Trainer
        // TODO: call trainerRepository.findByUserUsernameAndUserPassword("jane.trainer", "secret")
        // TODO: assert Optional.isPresent(), returned trainer matches persisted entity
    }

    @Test
    void findByUserUsernameAndUserPassword_shouldReturnEmpty_whenPasswordMismatch() {
        // TODO: persist User + Trainer with username "jane.trainer" and password "secret"
        // TODO: call trainerRepository.findByUserUsernameAndUserPassword("jane.trainer", "wrong")
        // TODO: assert Optional.isEmpty()
    }

    @Test
    void findAllBySpecializationName_shouldReturnTrainers_whenSpecializationMatches() {
        // TODO: persist TrainingType "Yoga" and two Trainers with that specialization; one with "Cardio"
        // TODO: call trainerRepository.findAllBySpecializationName("Yoga")
        // TODO: assert list size 2, all trainers have specialization.name "Yoga"
    }

    @Test
    void findAllBySpecializationName_shouldReturnEmptyList_whenNoMatchingSpecialization() {
        // TODO: persist trainers with specializations "Yoga" and "Cardio" only
        // TODO: call trainerRepository.findAllBySpecializationName("Pilates")
        // TODO: assert list is empty
    }

    @Test
    void findTrainersNotAssignedToTrainee_shouldReturnUnassignedTrainers() {
        // TODO: persist Trainee (username "john.doe") assigned to trainerA; also persist trainerB unassigned
        // TODO: call trainerRepository.findTrainersNotAssignedToTrainee("john.doe")
        // TODO: assert list contains trainerB only, excludes trainerA
    }

    @Test
    void findTrainersNotAssignedToTrainee_shouldReturnEmptyList_whenAllTrainersAssigned() {
        // TODO: persist one Trainee and assign every existing Trainer to that trainee
        // TODO: call trainerRepository.findTrainersNotAssignedToTrainee(trainee username)
        // TODO: assert list is empty
    }
}
