package com.elioth.epam.gymcrm.service;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.Training;
import com.elioth.epam.gymcrm.domain.TrainingType;
import com.elioth.epam.gymcrm.domain.User;
import com.elioth.epam.gymcrm.dto.request.CreateTrainingRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTrainingRequest;
import com.elioth.epam.gymcrm.dto.response.TrainingResponse;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.exception.InvalidEntityException;
import com.elioth.epam.gymcrm.exception.InvalidRequestException;
import com.elioth.epam.gymcrm.repository.TraineeRepository;
import com.elioth.epam.gymcrm.repository.TrainerRepository;
import com.elioth.epam.gymcrm.repository.TrainingRepository;
import com.elioth.epam.gymcrm.repository.TrainingTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    private static final Long TRAINER_ID = 1L;
    private static final Long OTHER_TRAINER_ID = 2L;
    private static final Long TRAINEE_ID = 10L;
    private static final Long TRAINING_ID = 100L;
    private static final Long TRAINING_TYPE_ID = 5L;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        trainingService.setTraineeRepository(traineeRepository);
        trainingService.setTrainerRepository(trainerRepository);
        trainingService.setTrainingRepository(trainingRepository);
        trainingService.setTrainingTypeRepository(trainingTypeRepository);
    }


    @Test
    void shouldCreateTrainingWhenRequestIsValidAndTrainerIdMatches() {
        CreateTrainingRequest request = validCreateRequest();
        Trainee trainee = buildTrainee(TRAINEE_ID, "Emily.Davis", "Emily", "Davis");
        Trainer trainer = buildTrainer(TRAINER_ID, "John.Doe", "John", "Doe");
        TrainingType trainingType = buildTrainingType(TRAINING_TYPE_ID, "YOGA");
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(TRAINER_ID)).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findById(TRAINING_TYPE_ID)).thenReturn(Optional.of(trainingType));
        when(trainingRepository.save(any(Training.class))).thenAnswer(invocation -> {
            Training training = invocation.getArgument(0);
            training.setTrainingId(TRAINING_ID);
            return training;
        });

        TrainingResponse response = trainingService.createTraining(TRAINER_ID, request);

        assertNotNull(response);
        assertEquals(TRAINING_ID, response.trainingId());
        assertEquals("Morning Yoga", response.trainingName());
        assertEquals(TRAINEE_ID, response.traineeId());
        assertEquals(TRAINER_ID, response.trainerId());

        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);
        verify(trainingRepository).save(captor.capture());
        Training saved = captor.getValue();
        assertEquals(trainee, saved.getTrainee());
        assertEquals(trainer, saved.getTrainer());
        assertEquals(trainingType, saved.getType());
        assertEquals("Morning Yoga", saved.getName());
        assertEquals(request.trainingDate(), saved.getDate());
        assertEquals(60L, saved.getDurationInMinutes());
    }

    @Test
    void shouldThrowInvalidEntityExceptionWhenCreateRequestIsInvalid() {
        CreateTrainingRequest request = new CreateTrainingRequest(
                TRAINEE_ID,
                TRAINER_ID,
                TRAINING_TYPE_ID,
                " ",
                LocalDate.of(2024, 6, 1),
                60L
        );

        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> trainingService.createTraining(TRAINER_ID, request)
        );
        assertEquals("Training name cannot be empty", exception.getMessage());
    }

    @Test
    void shouldThrowInvalidRequestExceptionWhenAuthenticatedTrainerIdDoesNotMatchRequestTrainerId() {
        CreateTrainingRequest request = validCreateRequest();

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> trainingService.createTraining(OTHER_TRAINER_ID, request)
        );
        assertEquals("Trainer id must match the authenticated trainer", exception.getMessage());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenTraineeNotFoundOnCreate() {
        CreateTrainingRequest request = validCreateRequest();
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> trainingService.createTraining(TRAINER_ID, request)
        );
        assertEquals("Trainee with id 10 not found", exception.getMessage());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenTrainerNotFoundOnCreate() {
        CreateTrainingRequest request = validCreateRequest();
        Trainee trainee = buildTrainee(TRAINEE_ID, "Emily.Davis", "Emily", "Davis");
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(TRAINER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> trainingService.createTraining(TRAINER_ID, request)
        );
        assertEquals("Trainer with id 1 not found", exception.getMessage());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenTrainingTypeNotFoundOnCreate() {
        CreateTrainingRequest request = validCreateRequest();
        Trainee trainee = buildTrainee(TRAINEE_ID, "Emily.Davis", "Emily", "Davis");
        Trainer trainer = buildTrainer(TRAINER_ID, "John.Doe", "John", "Doe");
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(TRAINER_ID)).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findById(TRAINING_TYPE_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> trainingService.createTraining(TRAINER_ID, request)
        );
        assertEquals("Training type with id 5 not found", exception.getMessage());
    }


    @Test
    void shouldUpdateTrainingWhenRequestIsValidAndTrainerOwnsTraining() {
        UpdateTrainingRequest request = validUpdateRequest();
        Training training = buildTraining(TRAINING_ID, TRAINER_ID);
        when(trainingRepository.findById(TRAINING_ID)).thenReturn(Optional.of(training));

        TrainingResponse response = trainingService.updateTraining(TRAINER_ID, TRAINING_ID, request);

        assertEquals(TRAINING_ID, response.trainingId());
        assertEquals("Evening Cardio", response.trainingName());
        assertEquals(request.date(), response.trainingDate());
        assertEquals(90L, response.duration());
        assertEquals("Evening Cardio", training.getName());
        assertEquals(request.date(), training.getDate());
        assertEquals(90L, training.getDurationInMinutes());
    }

    @Test
    void shouldThrowInvalidEntityExceptionWhenUpdateRequestIsInvalid() {
        InvalidEntityException exception = assertThrows(
                InvalidEntityException.class,
                () -> trainingService.updateTraining(TRAINER_ID, TRAINING_ID, null)
        );
        assertEquals("Request cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUpdatingNonExistentTraining() {
        UpdateTrainingRequest request = validUpdateRequest();
        when(trainingRepository.findById(TRAINING_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> trainingService.updateTraining(TRAINER_ID, TRAINING_ID, request)
        );
        assertEquals("Training with id 100 not found", exception.getMessage());
    }

    @Test
    void shouldThrowInvalidRequestExceptionWhenUpdatingTrainingNotOwnedByAuthenticatedTrainer() {
        UpdateTrainingRequest request = validUpdateRequest();
        Training training = buildTraining(TRAINING_ID, OTHER_TRAINER_ID);
        when(trainingRepository.findById(TRAINING_ID)).thenReturn(Optional.of(training));

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> trainingService.updateTraining(TRAINER_ID, TRAINING_ID, request)
        );
        assertEquals("Trainer cannot access this training", exception.getMessage());
    }


    @Test
    void shouldDeleteTrainingWhenTrainerOwnsTraining() {
        Training training = buildTraining(TRAINING_ID, TRAINER_ID);
        when(trainingRepository.findById(TRAINING_ID)).thenReturn(Optional.of(training));

        trainingService.deleteTraining(TRAINER_ID, TRAINING_ID);

        verify(trainingRepository).delete(training);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenDeletingNonExistentTraining() {
        when(trainingRepository.findById(TRAINING_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> trainingService.deleteTraining(TRAINER_ID, TRAINING_ID)
        );
        assertEquals("Training with id 100 not found", exception.getMessage());
    }

    @Test
    void shouldThrowInvalidRequestExceptionWhenDeletingTrainingNotOwnedByAuthenticatedTrainer() {
        Training training = buildTraining(TRAINING_ID, OTHER_TRAINER_ID);
        when(trainingRepository.findById(TRAINING_ID)).thenReturn(Optional.of(training));

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> trainingService.deleteTraining(TRAINER_ID, TRAINING_ID)
        );
        assertEquals("Trainer cannot access this training", exception.getMessage());
    }


    @Test
    void shouldGetTrainingByIdWhenTrainerOwnsTraining() {
        Training training = buildTraining(TRAINING_ID, TRAINER_ID);
        when(trainingRepository.findById(TRAINING_ID)).thenReturn(Optional.of(training));

        TrainingResponse response = trainingService.getTrainingById(TRAINER_ID, TRAINING_ID);

        assertEquals(TRAINING_ID, response.trainingId());
        assertEquals("Morning Yoga", response.trainingName());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenGettingNonExistentTraining() {
        when(trainingRepository.findById(TRAINING_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> trainingService.getTrainingById(TRAINER_ID, TRAINING_ID)
        );
        assertEquals("Training with id 100 not found", exception.getMessage());
    }

    @Test
    void shouldThrowInvalidRequestExceptionWhenGettingTrainingNotOwnedByAuthenticatedTrainer() {
        Training training = buildTraining(TRAINING_ID, OTHER_TRAINER_ID);
        when(trainingRepository.findById(TRAINING_ID)).thenReturn(Optional.of(training));

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> trainingService.getTrainingById(TRAINER_ID, TRAINING_ID)
        );
        assertEquals("Trainer cannot access this training", exception.getMessage());
    }


    @Test
    void shouldGetTrainingsByTraineeUsernameAndCriteriaWhenMatchesExist() {
        String traineeUsername = "Emily.Davis";
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);
        String trainerName = "John";
        String trainingTypeName = "Yoga";
        Training training = buildTraining(TRAINING_ID, TRAINER_ID);
        when(trainingRepository.findByTraineeUsernameAndCriteria(
                eq(traineeUsername),
                eq(from),
                eq(to),
                eq(trainerName),
                eq("YOGA")
        )).thenReturn(List.of(training));

        List<TrainingResponse> responses = trainingService.getTrainingsByTraineeUsernameAndCriteria(
                traineeUsername, from, to, trainerName, trainingTypeName
        );

        assertEquals(1, responses.size());
        assertEquals(TRAINING_ID, responses.get(0).trainingId());
    }


    @Test
    void shouldGetTrainingsByTrainerUsernameAndCriteriaWhenMatchesExist() {
        String trainerUsername = "John.Doe";
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);
        String traineeName = "Emily";
        Training training = buildTraining(TRAINING_ID, TRAINER_ID);
        when(trainingRepository.findByTrainerUsernameAndCriteria(
                eq(trainerUsername),
                eq(from),
                eq(to),
                eq(traineeName)
        )).thenReturn(List.of(training));

        List<TrainingResponse> responses = trainingService.getTrainingsByTrainerUsernameAndCriteria(
                trainerUsername, from, to, traineeName
        );

        assertEquals(1, responses.size());
        assertEquals(TRAINING_ID, responses.get(0).trainingId());
        assertEquals("John.Doe", responses.get(0).trainerUsername());
    }

    private CreateTrainingRequest validCreateRequest() {
        return new CreateTrainingRequest(
                TRAINEE_ID,
                TRAINER_ID,
                TRAINING_TYPE_ID,
                "Morning Yoga",
                LocalDate.of(2024, 6, 15),
                60L
        );
    }

    private UpdateTrainingRequest validUpdateRequest() {
        return new UpdateTrainingRequest(
                "Evening Cardio",
                LocalDate.of(2024, 7, 1),
                90L
        );
    }

    private TrainingType buildTrainingType(Long id, String name) {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(id);
        trainingType.setName(name);
        return trainingType;
    }

    private Trainee buildTrainee(Long traineeId, String username, String firstName, String lastName) {
        User user = buildUser(username, firstName, lastName);
        Trainee trainee = new Trainee();
        trainee.setTraineeId(traineeId);
        trainee.setUser(user);
        return trainee;
    }

    private Trainer buildTrainer(Long trainerId, String username, String firstName, String lastName) {
        User user = buildUser(username, firstName, lastName);
        Trainer trainer = new Trainer();
        trainer.setTrainerId(trainerId);
        trainer.setUser(user);
        return trainer;
    }

    private User buildUser(String username, String firstName, String lastName) {
        User user = new User();
        user.setUserId(100L);
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword("password");
        user.setActive(true);
        return user;
    }

    private Training buildTraining(Long trainingId, Long ownerTrainerId) {
        Trainee trainee = buildTrainee(TRAINEE_ID, "Emily.Davis", "Emily", "Davis");
        Trainer trainer = buildTrainer(ownerTrainerId, "John.Doe", "John", "Doe");
        TrainingType trainingType = buildTrainingType(TRAINING_TYPE_ID, "YOGA");

        Training training = new Training();
        training.setTrainingId(trainingId);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setType(trainingType);
        training.setName("Morning Yoga");
        training.setDate(LocalDate.of(2024, 6, 15));
        training.setDurationInMinutes(60L);
        return training;
    }
}
