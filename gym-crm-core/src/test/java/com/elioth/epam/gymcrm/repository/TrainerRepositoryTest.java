package com.elioth.epam.gymcrm.repository;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.TrainingType;
import com.elioth.epam.gymcrm.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class TrainerRepositoryTest {

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    private TrainingType persistTrainingType(String name) {
        TrainingType type = new TrainingType();
        type.setName(name);
        return trainingTypeRepository.save(type);
    }

    private Trainer persistTrainer(String username, String password, String specializationName) {
        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Trainer");
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(persistTrainingType(specializationName));
        return trainerRepository.save(trainer);
    }

    private Trainer persistTrainerWithNames(
            String username,
            String password,
            String firstName,
            String lastName,
            String specializationName
    ) {
        return persistTrainerWithSpecialization(
                username, password, firstName, lastName, persistTrainingType(specializationName)
        );
    }

    private Trainer persistTrainerWithSpecialization(
            String username,
            String password,
            String firstName,
            String lastName,
            TrainingType specialization
    ) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(specialization);
        return trainerRepository.save(trainer);
    }

    private Trainee persistTraineeWithTrainers(String username, Set<Trainer> trainers) {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername(username);
        user.setPassword("secret");
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setTrainers(trainers);
        return traineeRepository.save(trainee);
    }

    @Test
    void findByUserUsername_shouldReturnTrainer_whenUsernameExists() {
        persistTrainer("jane.trainer", "secret", "Yoga");

        Optional<Trainer> result = trainerRepository.findByUserUsername("jane.trainer");

        assertTrue(result.isPresent());
        assertEquals("jane.trainer", result.get().getUser().getUsername());
    }

    @Test
    void findByUserUsername_shouldReturnEmpty_whenUsernameNotFound() {
        persistTrainer("jane.trainer", "secret", "Yoga");

        Optional<Trainer> result = trainerRepository.findByUserUsername("missing.trainer");

        assertTrue(result.isEmpty());
    }

    @Test
    void findByUserUsernameAndUserPassword_shouldReturnTrainer_whenCredentialsMatch() {
        Trainer persisted = persistTrainer("jane.trainer", "secret", "Yoga");

        Optional<Trainer> result = trainerRepository.findByUserUsernameAndUserPassword("jane.trainer", "secret");

        assertTrue(result.isPresent());
        assertEquals(persisted.getTrainerId(), result.get().getTrainerId());
    }

    @Test
    void findByUserUsernameAndUserPassword_shouldReturnEmpty_whenPasswordMismatch() {
        persistTrainer("jane.trainer", "secret", "Yoga");

        Optional<Trainer> result = trainerRepository.findByUserUsernameAndUserPassword("jane.trainer", "wrong");

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllBySpecializationName_shouldReturnTrainers_whenSpecializationMatches() {
        TrainingType yoga = persistTrainingType("Yoga");
        TrainingType cardio = persistTrainingType("Cardio");
        persistTrainerWithSpecialization("yoga.trainer1", "secret", "Alice", "One", yoga);
        persistTrainerWithSpecialization("yoga.trainer2", "secret", "Bob", "Two", yoga);
        persistTrainerWithSpecialization("cardio.trainer", "secret", "Carol", "Three", cardio);

        List<Trainer> result = trainerRepository.findAllBySpecializationName("Yoga");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> "Yoga".equals(t.getSpecialization().getName())));
    }

    @Test
    void findAllBySpecializationName_shouldReturnEmptyList_whenNoMatchingSpecialization() {
        persistTrainerWithNames("yoga.trainer", "secret", "Alice", "One", "Yoga");
        persistTrainerWithNames("cardio.trainer", "secret", "Bob", "Two", "Cardio");

        List<Trainer> result = trainerRepository.findAllBySpecializationName("Pilates");

        assertTrue(result.isEmpty());
    }

    @Test
    void findTrainersNotAssignedToTrainee_shouldReturnUnassignedTrainers() {
        Trainer trainerA = persistTrainerWithNames("trainer.a", "secret", "Alice", "A", "Yoga");
        Trainer trainerB = persistTrainerWithNames("trainer.b", "secret", "Bob", "B", "Cardio");
        persistTraineeWithTrainers("john.doe", Set.of(trainerA));

        List<Trainer> result = trainerRepository.findTrainersNotAssignedToTrainee("john.doe");

        assertEquals(1, result.size());
        assertEquals(trainerB.getTrainerId(), result.getFirst().getTrainerId());
    }

    @Test
    void findTrainersNotAssignedToTrainee_shouldReturnEmptyList_whenAllTrainersAssigned() {
        Trainer trainerA = persistTrainerWithNames("trainer.a", "secret", "Alice", "A", "Yoga");
        Trainer trainerB = persistTrainerWithNames("trainer.b", "secret", "Bob", "B", "Cardio");
        persistTraineeWithTrainers("john.doe", Set.of(trainerA, trainerB));

        List<Trainer> result = trainerRepository.findTrainersNotAssignedToTrainee("john.doe");

        assertTrue(result.isEmpty());
    }
}
