package com.elioth.epam.gymcrm.repository;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.Training;
import com.elioth.epam.gymcrm.domain.TrainingType;
import com.elioth.epam.gymcrm.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class TrainingRepositoryTest {

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    private TrainingType persistTrainingType(String name) {
        TrainingType type = new TrainingType();
        type.setName(name);
        return trainingTypeRepository.save(type);
    }

    private Trainee persistTrainee(String username, String firstName, String lastName) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword("secret");
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        return traineeRepository.save(trainee);
    }

    private Trainer persistTrainer(String username, String firstName, String lastName, TrainingType specialization) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword("secret");
        user.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(specialization);
        return trainerRepository.save(trainer);
    }

    private Training persistTraining(
            Trainee trainee,
            Trainer trainer,
            TrainingType type,
            String name,
            LocalDate date
    ) {
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setType(type);
        training.setName(name);
        training.setDate(date);
        training.setDurationInMinutes(60L);
        return trainingRepository.save(training);
    }

    @Test
    void findAllByTraineeTraineeId_shouldReturnTrainings_whenTraineeHasSessions() {
        TrainingType yoga = persistTrainingType("Yoga");
        Trainee trainee = persistTrainee("john.doe", "John", "Doe");
        Trainee otherTrainee = persistTrainee("other.doe", "Other", "Doe");
        Trainer trainer = persistTrainer("jane.trainer", "Jane", "Smith", yoga);

        persistTraining(trainee, trainer, yoga, "Session 1", LocalDate.of(2024, 1, 10));
        persistTraining(trainee, trainer, yoga, "Session 2", LocalDate.of(2024, 1, 20));
        persistTraining(otherTrainee, trainer, yoga, "Other session", LocalDate.of(2024, 1, 15));

        List<Training> result = trainingRepository.findAllByTraineeTraineeId(trainee.getTraineeId());

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getTrainee().getTraineeId().equals(trainee.getTraineeId())));
    }

    @Test
    void findAllByTraineeTraineeId_shouldReturnEmptyList_whenTraineeHasNoSessions() {
        TrainingType yoga = persistTrainingType("Yoga");
        Trainee trainee = persistTrainee("john.doe", "John", "Doe");
        Trainer trainer = persistTrainer("jane.trainer", "Jane", "Smith", yoga);
        persistTraining(trainee, trainer, yoga, "Session 1", LocalDate.of(2024, 1, 10));

        List<Training> result = trainingRepository.findAllByTraineeTraineeId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByTrainerTrainerId_shouldReturnTrainings_whenTrainerHasSessions() {
        TrainingType yoga = persistTrainingType("Yoga");
        Trainee trainee = persistTrainee("john.doe", "John", "Doe");
        Trainer trainer = persistTrainer("jane.trainer", "Jane", "Smith", yoga);
        Trainer otherTrainer = persistTrainer("other.trainer", "Other", "Trainer", yoga);

        persistTraining(trainee, trainer, yoga, "Session 1", LocalDate.of(2024, 1, 10));
        persistTraining(trainee, trainer, yoga, "Session 2", LocalDate.of(2024, 1, 20));
        persistTraining(trainee, otherTrainer, yoga, "Other session", LocalDate.of(2024, 1, 15));

        List<Training> result = trainingRepository.findAllByTrainerTrainerId(trainer.getTrainerId());

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getTrainer().getTrainerId().equals(trainer.getTrainerId())));
    }

    @Test
    void findAllByTrainerTrainerId_shouldReturnEmptyList_whenTrainerHasNoSessions() {
        TrainingType yoga = persistTrainingType("Yoga");
        Trainee trainee = persistTrainee("john.doe", "John", "Doe");
        Trainer trainer = persistTrainer("jane.trainer", "Jane", "Smith", yoga);
        persistTraining(trainee, trainer, yoga, "Session 1", LocalDate.of(2024, 1, 10));

        List<Training> result = trainingRepository.findAllByTrainerTrainerId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByTypeName_shouldReturnTrainings_whenTypeMatches() {
        TrainingType yoga = persistTrainingType("Yoga");
        TrainingType cardio = persistTrainingType("Cardio");
        Trainee trainee = persistTrainee("john.doe", "John", "Doe");
        Trainer trainer = persistTrainer("jane.trainer", "Jane", "Smith", yoga);

        persistTraining(trainee, trainer, yoga, "Yoga 1", LocalDate.of(2024, 1, 10));
        persistTraining(trainee, trainer, yoga, "Yoga 2", LocalDate.of(2024, 1, 20));
        persistTraining(trainee, trainer, cardio, "Cardio 1", LocalDate.of(2024, 1, 15));

        List<Training> result = trainingRepository.findAllByTypeName("Yoga");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> "Yoga".equals(t.getType().getName())));
    }

    @Test
    void findAllByTypeName_shouldReturnEmptyList_whenTypeNotFound() {
        TrainingType yoga = persistTrainingType("Yoga");
        TrainingType cardio = persistTrainingType("Cardio");
        Trainee trainee = persistTrainee("john.doe", "John", "Doe");
        Trainer trainer = persistTrainer("jane.trainer", "Jane", "Smith", yoga);

        persistTraining(trainee, trainer, yoga, "Yoga 1", LocalDate.of(2024, 1, 10));
        persistTraining(trainee, trainer, cardio, "Cardio 1", LocalDate.of(2024, 1, 15));

        List<Training> result = trainingRepository.findAllByTypeName("Pilates");

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByDateBetween_shouldReturnTrainings_withinInclusiveRange() {
        TrainingType yoga = persistTrainingType("Yoga");
        Trainee trainee = persistTrainee("john.doe", "John", "Doe");
        Trainer trainer = persistTrainer("jane.trainer", "Jane", "Smith", yoga);

        persistTraining(trainee, trainer, yoga, "Early", LocalDate.of(2024, 1, 1));
        persistTraining(trainee, trainer, yoga, "Mid", LocalDate.of(2024, 1, 15));
        persistTraining(trainee, trainer, yoga, "Late", LocalDate.of(2024, 2, 1));

        List<Training> result = trainingRepository.findAllByDateBetween(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31)
        );

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t ->
                !t.getDate().isBefore(LocalDate.of(2024, 1, 1))
                        && !t.getDate().isAfter(LocalDate.of(2024, 1, 31))
        ));
    }

    @Test
    void findAllByDateBetween_shouldReturnEmptyList_whenNoTrainingsInRange() {
        TrainingType yoga = persistTrainingType("Yoga");
        Trainee trainee = persistTrainee("john.doe", "John", "Doe");
        Trainer trainer = persistTrainer("jane.trainer", "Jane", "Smith", yoga);

        persistTraining(trainee, trainer, yoga, "Early", LocalDate.of(2024, 1, 1));
        persistTraining(trainee, trainer, yoga, "Late", LocalDate.of(2024, 3, 1));

        List<Training> result = trainingRepository.findAllByDateBetween(
                LocalDate.of(2024, 4, 1),
                LocalDate.of(2024, 4, 30)
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void findByTraineeUsernameAndCriteria_shouldReturnMatchingTrainings_whenFiltersApplied() {
        TrainingType yoga = persistTrainingType("Yoga");
        TrainingType cardio = persistTrainingType("Cardio");
        Trainee trainee = persistTrainee("john.doe", "John", "Doe");
        Trainer jane = persistTrainer("jane.trainer", "Jane", "Smith", yoga);
        Trainer bob = persistTrainer("bob.trainer", "Bob", "Lee", cardio);

        persistTraining(trainee, jane, yoga, "Match", LocalDate.of(2024, 6, 10));
        persistTraining(trainee, jane, cardio, "Wrong type", LocalDate.of(2024, 6, 11));
        persistTraining(trainee, bob, yoga, "Wrong trainer", LocalDate.of(2024, 6, 12));
        persistTraining(trainee, jane, yoga, "Out of range", LocalDate.of(2024, 8, 1));

        List<Training> result = trainingRepository.findByTraineeUsernameAndCriteria(
                "john.doe",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 6, 30),
                "Jane",
                "Yoga"
        );

        assertEquals(1, result.size());
        assertEquals("Match", result.getFirst().getName());
    }

    @Test
    void findByTraineeUsernameAndCriteria_shouldReturnEmptyList_whenNoTrainingsMatchCriteria() {
        TrainingType yoga = persistTrainingType("Yoga");
        Trainee trainee = persistTrainee("john.doe", "John", "Doe");
        Trainer jane = persistTrainer("jane.trainer", "Jane", "Smith", yoga);

        persistTraining(trainee, jane, yoga, "Session", LocalDate.of(2024, 6, 10));

        List<Training> restrictive = trainingRepository.findByTraineeUsernameAndCriteria(
                "john.doe",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 6, 30),
                "Jane",
                "Cardio"
        );
        assertTrue(restrictive.isEmpty());

        List<Training> allForTrainee = trainingRepository.findByTraineeUsernameAndCriteria(
                "john.doe", null, null, null, null
        );
        assertEquals(1, allForTrainee.size());

        List<Training> otherTrainee = trainingRepository.findByTraineeUsernameAndCriteria(
                "missing.user", null, null, null, null
        );
        assertTrue(otherTrainee.isEmpty());
    }

    @Test
    void findByTrainerUsernameAndCriteria_shouldReturnMatchingTrainings_whenFiltersApplied() {
        TrainingType yoga = persistTrainingType("Yoga");
        Trainee john = persistTrainee("john.doe", "John", "Doe");
        Trainee mary = persistTrainee("mary.doe", "Mary", "Doe");
        Trainer trainer = persistTrainer("jane.trainer", "Jane", "Trainer", yoga);

        persistTraining(john, trainer, yoga, "John session", LocalDate.of(2024, 6, 10));
        persistTraining(mary, trainer, yoga, "Mary session", LocalDate.of(2024, 6, 11));
        persistTraining(john, trainer, yoga, "Out of range", LocalDate.of(2024, 8, 1));

        List<Training> result = trainingRepository.findByTrainerUsernameAndCriteria(
                "jane.trainer",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 6, 30),
                "John"
        );

        assertEquals(1, result.size());
        assertEquals("John session", result.getFirst().getName());
    }

    @Test
    void findByTrainerUsernameAndCriteria_shouldReturnEmptyList_whenNoTrainingsMatchCriteria() {
        TrainingType yoga = persistTrainingType("Yoga");
        Trainee john = persistTrainee("john.doe", "John", "Doe");
        Trainer trainer = persistTrainer("jane.trainer", "Jane", "Trainer", yoga);

        persistTraining(john, trainer, yoga, "John session", LocalDate.of(2024, 6, 10));

        List<Training> restrictive = trainingRepository.findByTrainerUsernameAndCriteria(
                "jane.trainer",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 6, 30),
                "Mary"
        );
        assertTrue(restrictive.isEmpty());

        List<Training> allInRange = trainingRepository.findByTrainerUsernameAndCriteria(
                "jane.trainer",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 6, 30),
                null
        );
        assertEquals(1, allInRange.size());

        List<Training> otherTrainer = trainingRepository.findByTrainerUsernameAndCriteria(
                "missing.trainer", null, null, null
        );
        assertTrue(otherTrainer.isEmpty());
    }
}
