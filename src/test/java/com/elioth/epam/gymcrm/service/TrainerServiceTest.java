package com.elioth.epam.gymcrm.service;

import com.elioth.epam.gymcrm.repository.TrainerRepository;
import com.elioth.epam.gymcrm.repository.TrainingTypeRepository;
import com.elioth.epam.gymcrm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Disabled("Practice skeleton - implement one test at a time, then remove @Disabled from class")
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        trainerService.setTrainerRepository(trainerRepository);
        trainerService.setUserRepository(userRepository);
        trainerService.setTrainingTypeRepository(trainingTypeRepository);
    }

    // --- createProfile ---

    @Test
    @Disabled("Practice skeleton")
    void shouldCreateProfileWhenRequestIsValid() {
        // Arrange
        // TODO: create valid CreateTrainerRequest (firstName, lastName, trainingTypeId)
        // TODO: create TrainingType for trainingTypeId
        // TODO: mock userRepository.countByFirstNameAndLastName(...) -> 0
        // TODO: mock trainingTypeRepository.findById(trainingTypeId) -> Optional.of(trainingType)
        // TODO: mock trainerRepository.save(...) -> saved Trainer with User

        // Act
        // TODO: CreatedTrainerResponse response = trainerService.createProfile(request)

        // Assert
        // TODO: assert response is not null
        // TODO: verify trainerRepository.save(...) was called once
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowInvalidRequestExceptionWhenCreateRequestIsNull() {
        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, () -> trainerService.createProfile(null))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowEntityNotFoundExceptionWhenTrainingTypeNotFoundOnCreate() {
        // Arrange
        // TODO: create valid CreateTrainerRequest with unknown trainingTypeId
        // TODO: mock trainingTypeRepository.findById(trainingTypeId) -> Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> trainerService.createProfile(request))
    }

    // --- updateProfile ---

    @Test
    @Disabled("Practice skeleton")
    void shouldUpdateProfileWhenRequestIsValid() {
        // Arrange
        // TODO: create valid UpdateTrainerRequest
        // TODO: create existing Trainer with User and TrainingType
        // TODO: create TrainingType for new trainingTypeId
        // TODO: mock trainerRepository.findById(trainerId) -> Optional.of(trainer)
        // TODO: mock trainingTypeRepository.findById(trainingTypeId) -> Optional.of(trainingType)

        // Act
        // TODO: TrainerResponse response = trainerService.updateProfile(trainerId, request)

        // Assert
        // TODO: assert response fields match updated values
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowInvalidRequestExceptionWhenUpdateRequestIsNull() {
        // Arrange
        // TODO: define trainerId

        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, () -> trainerService.updateProfile(trainerId, null))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowEntityNotFoundExceptionWhenUpdatingNonExistentTrainer() {
        // Arrange
        // TODO: create valid UpdateTrainerRequest
        // TODO: mock trainerRepository.findById(trainerId) -> Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> trainerService.updateProfile(trainerId, request))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowEntityNotFoundExceptionWhenTrainingTypeNotFoundOnUpdate() {
        // Arrange
        // TODO: create valid UpdateTrainerRequest
        // TODO: create existing Trainer
        // TODO: mock trainerRepository.findById(trainerId) -> Optional.of(trainer)
        // TODO: mock trainingTypeRepository.findById(trainingTypeId) -> Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> trainerService.updateProfile(trainerId, request))
    }

    // --- getProfileById ---

    @Test
    @Disabled("Practice skeleton")
    void shouldGetProfileByIdWhenTrainerExists() {
        // Arrange
        // TODO: create existing Trainer with User and TrainingType
        // TODO: mock trainerRepository.findById(trainerId) -> Optional.of(trainer)

        // Act
        // TODO: TrainerResponse response = trainerService.getProfileById(trainerId)

        // Assert
        // TODO: assert response id and user fields match expected values
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowEntityNotFoundExceptionWhenGettingProfileByNonExistentId() {
        // Arrange
        // TODO: mock trainerRepository.findById(trainerId) -> Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> trainerService.getProfileById(trainerId))
    }

    // --- getProfileByUsername ---

    @Test
    @Disabled("Practice skeleton")
    void shouldGetProfileByUsernameWhenTrainerExists() {
        // Arrange
        // TODO: create existing Trainer with User (username set)
        // TODO: mock trainerRepository.findByUserUsername(username) -> Optional.of(trainer)

        // Act
        // TODO: TrainerResponse response = trainerService.getProfileByUsername(username)

        // Assert
        // TODO: assert response username matches expected value
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowInvalidRequestExceptionWhenGettingProfileByBlankUsername() {
        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, () -> trainerService.getProfileByUsername(" "))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowEntityNotFoundExceptionWhenGettingProfileByUnknownUsername() {
        // Arrange
        // TODO: mock trainerRepository.findByUserUsername(username) -> Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> trainerService.getProfileByUsername(username))
    }

    // --- changePassword ---

    @Test
    @Disabled("Practice skeleton")
    void shouldChangePasswordWhenOldPasswordIsCorrect() {
        // Arrange
        // TODO: create Trainer with User (known password)
        // TODO: create ChangePasswordRequest with matching oldPassword and valid newPassword
        // TODO: mock trainerRepository.findById(trainerId) -> Optional.of(trainer)

        // Act
        // TODO: TrainerResponse response = trainerService.changePassword(trainerId, request)

        // Assert
        // TODO: assert trainer user password equals newPassword
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowInvalidRequestExceptionWhenChangePasswordRequestIsNull() {
        // Arrange
        // TODO: define trainerId

        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, () -> trainerService.changePassword(trainerId, null))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowIncorrectPasswordExceptionWhenOldPasswordDoesNotMatch() {
        // Arrange
        // TODO: create Trainer with User (password "oldPass")
        // TODO: create ChangePasswordRequest with wrong oldPassword
        // TODO: mock trainerRepository.findById(trainerId) -> Optional.of(trainer)

        // Act & Assert
        // TODO: assertThrows(IncorrectPasswordException.class, () -> trainerService.changePassword(trainerId, request))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowEntityNotFoundExceptionWhenChangingPasswordForNonExistentTrainer() {
        // Arrange
        // TODO: create valid ChangePasswordRequest
        // TODO: mock trainerRepository.findById(trainerId) -> Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> trainerService.changePassword(trainerId, request))
    }

    // --- findBySpecializationName ---

    @Test
    @Disabled("Practice skeleton")
    void shouldFindTrainersBySpecializationNameWhenTrainersExist() {
        // Arrange
        // TODO: define specialization name (e.g. "Yoga")
        // TODO: create List<Trainer> with matching specialization
        // TODO: mock trainerRepository.findAllBySpecializationName(name.toUpperCase()) -> trainers

        // Act
        // TODO: List<TrainerResponse> responses = trainerService.findBySpecializationName(name)

        // Assert
        // TODO: assert responses size equals expected count
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowInvalidRequestExceptionWhenSpecializationNameIsBlank() {
        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, () -> trainerService.findBySpecializationName(" "))
    }

    // --- getTrainersNotAssignedToTrainee ---

    @Test
    @Disabled("Practice skeleton")
    void shouldGetTrainersNotAssignedToTraineeWhenTraineeUsernameIsValid() {
        // Arrange
        // TODO: define traineeUsername
        // TODO: create List<Trainer> not assigned to trainee
        // TODO: mock trainerRepository.findTrainersNotAssignedToTrainee(traineeUsername) -> trainers

        // Act
        // TODO: List<TrainerResponse> responses = trainerService.getTrainersNotAssignedToTrainee(traineeUsername)

        // Assert
        // TODO: assert responses size equals expected count
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowInvalidRequestExceptionWhenTraineeUsernameIsBlank() {
        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, () -> trainerService.getTrainersNotAssignedToTrainee(" "))
    }

    // --- activate ---

    @Test
    @Disabled("Practice skeleton")
    void shouldActivateTrainerWhenCurrentlyInactive() {
        // Arrange
        // TODO: create Trainer with User (active = false)
        // TODO: mock trainerRepository.findById(trainerId) -> Optional.of(trainer)

        // Act
        // TODO: trainerService.activate(trainerId)

        // Assert
        // TODO: assert trainer user active is true
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowInvalidRequestExceptionWhenActivatingAlreadyActiveTrainer() {
        // Arrange
        // TODO: create Trainer with User (active = true)
        // TODO: mock trainerRepository.findById(trainerId) -> Optional.of(trainer)

        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, () -> trainerService.activate(trainerId))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowEntityNotFoundExceptionWhenActivatingNonExistentTrainer() {
        // Arrange
        // TODO: mock trainerRepository.findById(trainerId) -> Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> trainerService.activate(trainerId))
    }

    // --- deactivate ---

    @Test
    @Disabled("Practice skeleton")
    void shouldDeactivateTrainerWhenCurrentlyActive() {
        // Arrange
        // TODO: create Trainer with User (active = true)
        // TODO: mock trainerRepository.findById(trainerId) -> Optional.of(trainer)

        // Act
        // TODO: trainerService.deactivate(trainerId)

        // Assert
        // TODO: assert trainer user active is false
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowInvalidRequestExceptionWhenDeactivatingAlreadyInactiveTrainer() {
        // Arrange
        // TODO: create Trainer with User (active = false)
        // TODO: mock trainerRepository.findById(trainerId) -> Optional.of(trainer)

        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, () -> trainerService.deactivate(trainerId))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowEntityNotFoundExceptionWhenDeactivatingNonExistentTrainer() {
        // Arrange
        // TODO: mock trainerRepository.findById(trainerId) -> Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> trainerService.deactivate(trainerId))
    }
}
