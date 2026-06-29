package com.elioth.epam.gymcrm.service;

import com.elioth.epam.gymcrm.repository.TraineeRepository;
import com.elioth.epam.gymcrm.repository.TrainerRepository;
import com.elioth.epam.gymcrm.repository.TrainingRepository;
import com.elioth.epam.gymcrm.repository.TrainingTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Disabled("Practice skeleton - implement one test at a time, then remove @Disabled from class")
class TrainingServiceTest {

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

    // --- createTraining ---

    @Test
    @Disabled("Practice skeleton")
    void shouldCreateTrainingWhenRequestIsValidAndTrainerIdMatches() {
        // Arrange
        // TODO: define authenticated trainerId
        // TODO: create valid CreateTrainingRequest with matching trainerId
        // TODO: create Trainee, Trainer, TrainingType entities
        // TODO: mock traineeRepository.findById(traineeId) -> Optional.of(trainee)
        // TODO: mock trainerRepository.findById(trainerId) -> Optional.of(trainer)
        // TODO: mock trainingTypeRepository.findById(trainingTypeId) -> Optional.of(trainingType)
        // TODO: mock trainingRepository.save(...) -> saved Training

        // Act
        // TODO: TrainingResponse response = trainingService.createTraining(trainerId, request)

        // Assert
        // TODO: assert response is not null
        // TODO: verify trainingRepository.save(...) was called once
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowInvalidEntityExceptionWhenCreateRequestIsInvalid() {
        // Arrange
        // TODO: define authenticated trainerId
        // TODO: create invalid CreateTrainingRequest (e.g. null trainingName or duration <= 0)

        // Act & Assert
        // TODO: assertThrows(InvalidEntityException.class, () -> trainingService.createTraining(trainerId, request))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowInvalidRequestExceptionWhenAuthenticatedTrainerIdDoesNotMatchRequestTrainerId() {
        // Arrange
        // TODO: define authenticated trainerId (e.g. 1L)
        // TODO: create CreateTrainingRequest with different trainerId (e.g. 2L)

        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, () -> trainingService.createTraining(trainerId, request))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowEntityNotFoundExceptionWhenTraineeNotFoundOnCreate() {
        // Arrange
        // TODO: define authenticated trainerId matching request.trainerId()
        // TODO: create valid CreateTrainingRequest
        // TODO: mock traineeRepository.findById(traineeId) -> Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> trainingService.createTraining(trainerId, request))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowEntityNotFoundExceptionWhenTrainerNotFoundOnCreate() {
        // Arrange
        // TODO: define authenticated trainerId matching request.trainerId()
        // TODO: create valid CreateTrainingRequest
        // TODO: mock traineeRepository.findById(traineeId) -> Optional.of(trainee)
        // TODO: mock trainerRepository.findById(trainerId) -> Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> trainingService.createTraining(trainerId, request))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowEntityNotFoundExceptionWhenTrainingTypeNotFoundOnCreate() {
        // Arrange
        // TODO: define authenticated trainerId matching request.trainerId()
        // TODO: create valid CreateTrainingRequest
        // TODO: mock traineeRepository.findById(traineeId) -> Optional.of(trainee)
        // TODO: mock trainerRepository.findById(trainerId) -> Optional.of(trainer)
        // TODO: mock trainingTypeRepository.findById(trainingTypeId) -> Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> trainingService.createTraining(trainerId, request))
    }

    // --- updateTraining ---

    @Test
    @Disabled("Practice skeleton")
    void shouldUpdateTrainingWhenRequestIsValidAndTrainerOwnsTraining() {
        // Arrange
        // TODO: define authenticated trainerId and trainingId
        // TODO: create valid UpdateTrainingRequest
        // TODO: create Training owned by trainerId
        // TODO: mock trainingRepository.findById(trainingId) -> Optional.of(training)

        // Act
        // TODO: TrainingResponse response = trainingService.updateTraining(trainerId, trainingId, request)

        // Assert
        // TODO: assert response fields match updated values
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowInvalidEntityExceptionWhenUpdateRequestIsInvalid() {
        // Arrange
        // TODO: define authenticated trainerId and trainingId

        // Act & Assert
        // TODO: assertThrows(InvalidEntityException.class, () -> trainingService.updateTraining(trainerId, trainingId, null))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowEntityNotFoundExceptionWhenUpdatingNonExistentTraining() {
        // Arrange
        // TODO: define authenticated trainerId and trainingId
        // TODO: create valid UpdateTrainingRequest
        // TODO: mock trainingRepository.findById(trainingId) -> Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> trainingService.updateTraining(trainerId, trainingId, request))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowInvalidRequestExceptionWhenUpdatingTrainingNotOwnedByAuthenticatedTrainer() {
        // Arrange
        // TODO: define authenticated trainerId (e.g. 1L)
        // TODO: create Training owned by different trainer (e.g. trainerId 2L)
        // TODO: create valid UpdateTrainingRequest
        // TODO: mock trainingRepository.findById(trainingId) -> Optional.of(training)

        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, () -> trainingService.updateTraining(trainerId, trainingId, request))
    }

    // --- deleteTraining ---

    @Test
    @Disabled("Practice skeleton")
    void shouldDeleteTrainingWhenTrainerOwnsTraining() {
        // Arrange
        // TODO: define authenticated trainerId and trainingId
        // TODO: create Training owned by trainerId
        // TODO: mock trainingRepository.findById(trainingId) -> Optional.of(training)

        // Act
        // TODO: trainingService.deleteTraining(trainerId, trainingId)

        // Assert
        // TODO: verify trainingRepository.delete(training) was called once
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowEntityNotFoundExceptionWhenDeletingNonExistentTraining() {
        // Arrange
        // TODO: define authenticated trainerId and trainingId
        // TODO: mock trainingRepository.findById(trainingId) -> Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> trainingService.deleteTraining(trainerId, trainingId))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowInvalidRequestExceptionWhenDeletingTrainingNotOwnedByAuthenticatedTrainer() {
        // Arrange
        // TODO: define authenticated trainerId (e.g. 1L)
        // TODO: create Training owned by different trainer (e.g. trainerId 2L)
        // TODO: mock trainingRepository.findById(trainingId) -> Optional.of(training)

        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, () -> trainingService.deleteTraining(trainerId, trainingId))
    }

    // --- getTrainingById ---

    @Test
    @Disabled("Practice skeleton")
    void shouldGetTrainingByIdWhenTrainerOwnsTraining() {
        // Arrange
        // TODO: define authenticated trainerId and trainingId
        // TODO: create Training owned by trainerId
        // TODO: mock trainingRepository.findById(trainingId) -> Optional.of(training)

        // Act
        // TODO: TrainingResponse response = trainingService.getTrainingById(trainerId, trainingId)

        // Assert
        // TODO: assert response id matches trainingId
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowEntityNotFoundExceptionWhenGettingNonExistentTraining() {
        // Arrange
        // TODO: define authenticated trainerId and trainingId
        // TODO: mock trainingRepository.findById(trainingId) -> Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> trainingService.getTrainingById(trainerId, trainingId))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowInvalidRequestExceptionWhenGettingTrainingNotOwnedByAuthenticatedTrainer() {
        // Arrange
        // TODO: define authenticated trainerId (e.g. 1L)
        // TODO: create Training owned by different trainer (e.g. trainerId 2L)
        // TODO: mock trainingRepository.findById(trainingId) -> Optional.of(training)

        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, () -> trainingService.getTrainingById(trainerId, trainingId))
    }

    // --- getTrainingsByTraineeUsernameAndCriteria ---

    @Test
    @Disabled("Practice skeleton")
    void shouldGetTrainingsByTraineeUsernameAndCriteriaWhenMatchesExist() {
        // Arrange
        // TODO: define traineeUsername, from, to, trainerName, trainingTypeName
        // TODO: create List<Training> matching criteria
        // TODO: mock trainingRepository.findByTraineeUsernameAndCriteria(
        //           traineeUsername, from, to, trainerName, trainingTypeName.toUpperCase()) -> trainings

        // Act
        // TODO: List<TrainingResponse> responses = trainingService.getTrainingsByTraineeUsernameAndCriteria(
        //           traineeUsername, from, to, trainerName, trainingTypeName)

        // Assert
        // TODO: assert responses size equals expected count
    }

    // --- getTrainingsByTrainerUsernameAndCriteria ---

    @Test
    @Disabled("Practice skeleton")
    void shouldGetTrainingsByTrainerUsernameAndCriteriaWhenMatchesExist() {
        // Arrange
        // TODO: define trainerUsername, from, to, traineeName
        // TODO: create List<Training> matching criteria
        // TODO: mock trainingRepository.findByTrainerUsernameAndCriteria(
        //           trainerUsername, from, to, traineeName) -> trainings

        // Act
        // TODO: List<TrainingResponse> responses = trainingService.getTrainingsByTrainerUsernameAndCriteria(
        //           trainerUsername, from, to, traineeName)

        // Assert
        // TODO: assert responses size equals expected count
    }
}
