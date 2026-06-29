package com.elioth.epam.gymcrm.service;

import com.elioth.epam.gymcrm.repository.TraineeRepository;
import com.elioth.epam.gymcrm.repository.TrainerRepository;
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
class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TraineeService traineeService;

    @BeforeEach
    void setUp() {
        traineeService.setTraineeRepository(traineeRepository);
        traineeService.setTrainerRepository(trainerRepository);
        traineeService.setUserRepository(userRepository);
    }

    // --- createProfile ---

    @Test
    @Disabled("Practice skeleton")
    void shouldCreateProfileWhenRequestIsValid() {
        // Arrange
        // TODO: create valid CreateTraineeRequest (firstName, lastName, address, birthDate)
        // TODO: mock userRepository.countByFirstNameAndLastName(...) -> 0
        // TODO: mock traineeRepository.save(...) -> saved Trainee with User

        // Act
        // TODO: CreatedTraineeResponse response = traineeService.createProfile(request)

        // Assert
        // TODO: assert response is not null
        // TODO: verify traineeRepository.save(...) was called once
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowInvalidRequestExceptionWhenCreateRequestIsNull() {
        // Arrange
        // TODO: no repository setup required

        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, () -> traineeService.createProfile(null))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowInvalidRequestExceptionWhenCreateRequestHasBlankFirstName() {
        // Arrange
        // TODO: create CreateTraineeRequest with blank firstName

        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, () -> traineeService.createProfile(request))
    }

    // --- updateProfile ---

    @Test
    @Disabled("Practice skeleton")
    void shouldUpdateProfileWhenRequestIsValid() {
        // Arrange
        // TODO: create valid UpdateTraineeRequest
        // TODO: create existing Trainee with User
        // TODO: mock traineeRepository.findById(traineeId) -> Optional.of(trainee)

        // Act
        // TODO: TraineeResponse response = traineeService.updateProfile(traineeId, request)

        // Assert
        // TODO: assert response fields match updated values
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowInvalidRequestExceptionWhenUpdateRequestIsNull() {
        // Arrange
        // TODO: define traineeId

        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, () -> traineeService.updateProfile(traineeId, null))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowEntityNotFoundExceptionWhenUpdatingNonExistentTrainee() {
        // Arrange
        // TODO: create valid UpdateTraineeRequest
        // TODO: mock traineeRepository.findById(traineeId) -> Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> traineeService.updateProfile(traineeId, request))
    }

    // --- deleteProfile ---

    @Test
    @Disabled("Practice skeleton")
    void shouldDeleteProfileWhenTraineeExists() {
        // Arrange
        // TODO: create existing Trainee
        // TODO: mock traineeRepository.findById(traineeId) -> Optional.of(trainee)

        // Act
        // TODO: traineeService.deleteProfile(traineeId)

        // Assert
        // TODO: verify traineeRepository.delete(trainee) was called once
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowEntityNotFoundExceptionWhenDeletingNonExistentTrainee() {
        // Arrange
        // TODO: mock traineeRepository.findById(traineeId) -> Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> traineeService.deleteProfile(traineeId))
    }

    // --- getProfileById ---

    @Test
    @Disabled("Practice skeleton")
    void shouldGetProfileByIdWhenTraineeExists() {
        // Arrange
        // TODO: create existing Trainee with User
        // TODO: mock traineeRepository.findById(traineeId) -> Optional.of(trainee)

        // Act
        // TODO: TraineeResponse response = traineeService.getProfileById(traineeId)

        // Assert
        // TODO: assert response id and user fields match expected values
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowEntityNotFoundExceptionWhenGettingProfileByNonExistentId() {
        // Arrange
        // TODO: mock traineeRepository.findById(traineeId) -> Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> traineeService.getProfileById(traineeId))
    }

    // --- getProfileByUsername ---

    @Test
    @Disabled("Practice skeleton")
    void shouldGetProfileByUsernameWhenTraineeExists() {
        // Arrange
        // TODO: create existing Trainee with User (username set)
        // TODO: mock traineeRepository.findByUserUsername(username) -> Optional.of(trainee)

        // Act
        // TODO: TraineeResponse response = traineeService.getProfileByUsername(username)

        // Assert
        // TODO: assert response username matches expected value
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowInvalidRequestExceptionWhenGettingProfileByBlankUsername() {
        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, () -> traineeService.getProfileByUsername(" "))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowEntityNotFoundExceptionWhenGettingProfileByUnknownUsername() {
        // Arrange
        // TODO: mock traineeRepository.findByUserUsername(username) -> Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> traineeService.getProfileByUsername(username))
    }

    // --- changePassword ---

    @Test
    @Disabled("Practice skeleton")
    void shouldChangePasswordWhenOldPasswordIsCorrect() {
        // Arrange
        // TODO: create Trainee with User (known password)
        // TODO: create ChangePasswordRequest with matching oldPassword and valid newPassword
        // TODO: mock traineeRepository.findById(traineeId) -> Optional.of(trainee)

        // Act
        // TODO: TraineeResponse response = traineeService.changePassword(traineeId, request)

        // Assert
        // TODO: assert trainee user password equals newPassword
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowInvalidRequestExceptionWhenChangePasswordRequestIsNull() {
        // Arrange
        // TODO: define traineeId

        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, () -> traineeService.changePassword(traineeId, null))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowIncorrectPasswordExceptionWhenOldPasswordDoesNotMatch() {
        // Arrange
        // TODO: create Trainee with User (password "oldPass")
        // TODO: create ChangePasswordRequest with wrong oldPassword
        // TODO: mock traineeRepository.findById(traineeId) -> Optional.of(trainee)

        // Act & Assert
        // TODO: assertThrows(IncorrectPasswordException.class, () -> traineeService.changePassword(traineeId, request))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowEntityNotFoundExceptionWhenChangingPasswordForNonExistentTrainee() {
        // Arrange
        // TODO: create valid ChangePasswordRequest
        // TODO: mock traineeRepository.findById(traineeId) -> Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> traineeService.changePassword(traineeId, request))
    }

    // --- updateTrainersToTrainee ---

    @Test
    @Disabled("Practice skeleton")
    void shouldUpdateTrainersToTraineeWhenAllTrainerIdsExist() {
        // Arrange
        // TODO: create Trainee with empty trainers collection
        // TODO: create List<Long> trainerIds with valid ids
        // TODO: create List<Trainer> matching all ids
        // TODO: mock traineeRepository.findById(traineeId) -> Optional.of(trainee)
        // TODO: mock trainerRepository.findAllById(trainerIds) -> trainers

        // Act
        // TODO: traineeService.updateTrainersToTrainee(traineeId, trainerIds)

        // Assert
        // TODO: assert trainee trainers collection contains all expected trainers
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowInvalidRequestExceptionWhenTrainerIdsListIsNull() {
        // Arrange
        // TODO: define traineeId

        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, () -> traineeService.updateTrainersToTrainee(traineeId, null))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowEntityNotFoundExceptionWhenTraineeNotFoundForTrainerUpdate() {
        // Arrange
        // TODO: create List<Long> trainerIds
        // TODO: mock traineeRepository.findById(traineeId) -> Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> traineeService.updateTrainersToTrainee(traineeId, trainerIds))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowEntityNotFoundExceptionWhenOneOrMoreTrainersNotFound() {
        // Arrange
        // TODO: create Trainee
        // TODO: create List<Long> trainerIds (size > returned trainers size)
        // TODO: mock traineeRepository.findById(traineeId) -> Optional.of(trainee)
        // TODO: mock trainerRepository.findAllById(trainerIds) -> partial list

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> traineeService.updateTrainersToTrainee(traineeId, trainerIds))
    }

    // --- activate ---

    @Test
    @Disabled("Practice skeleton")
    void shouldActivateTraineeWhenCurrentlyInactive() {
        // Arrange
        // TODO: create Trainee with User (active = false)
        // TODO: mock traineeRepository.findById(traineeId) -> Optional.of(trainee)

        // Act
        // TODO: traineeService.activate(traineeId)

        // Assert
        // TODO: assert trainee user active is true
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowInvalidRequestExceptionWhenActivatingAlreadyActiveTrainee() {
        // Arrange
        // TODO: create Trainee with User (active = true)
        // TODO: mock traineeRepository.findById(traineeId) -> Optional.of(trainee)

        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, () -> traineeService.activate(traineeId))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowEntityNotFoundExceptionWhenActivatingNonExistentTrainee() {
        // Arrange
        // TODO: mock traineeRepository.findById(traineeId) -> Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> traineeService.activate(traineeId))
    }

    // --- deactivate ---

    @Test
    @Disabled("Practice skeleton")
    void shouldDeactivateTraineeWhenCurrentlyActive() {
        // Arrange
        // TODO: create Trainee with User (active = true)
        // TODO: mock traineeRepository.findById(traineeId) -> Optional.of(trainee)

        // Act
        // TODO: traineeService.deactivate(traineeId)

        // Assert
        // TODO: assert trainee user active is false
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowInvalidRequestExceptionWhenDeactivatingAlreadyInactiveTrainee() {
        // Arrange
        // TODO: create Trainee with User (active = false)
        // TODO: mock traineeRepository.findById(traineeId) -> Optional.of(trainee)

        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, () -> traineeService.deactivate(traineeId))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowEntityNotFoundExceptionWhenDeactivatingNonExistentTrainee() {
        // Arrange
        // TODO: mock traineeRepository.findById(traineeId) -> Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> traineeService.deactivate(traineeId))
    }
}
