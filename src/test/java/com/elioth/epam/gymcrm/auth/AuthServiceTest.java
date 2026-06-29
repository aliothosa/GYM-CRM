package com.elioth.epam.gymcrm.auth;

import com.elioth.epam.gymcrm.service.AuthService;
import com.elioth.epam.gymcrm.repository.TraineeRepository;
import com.elioth.epam.gymcrm.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Disabled("Practice skeleton - implement one test at a time, then remove @Disabled from class")
class AuthServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService.setTraineeRepository(traineeRepository);
        authService.setTrainerRepository(trainerRepository);
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldLoginTraineeWhenCredentialsAreValidAndUserIsActive() {
        // Arrange
        // TODO: create Trainee with active User (username/password match)
        // TODO: mock traineeRepository.findByUserUsernameAndUserPassword(...) -> Optional.of(trainee)

        // Act
        // TODO: AuthSession session = authService.loginTrainee("Emily.Davis", "pass123");

        // Assert
        // TODO: assert session.role() == Role.TRAINEE
        // TODO: assert session.username() equals expected username
        // TODO: assert session.id() equals traineeId
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldLoginTrainerWhenCredentialsAreValidAndUserIsActive() {
        // Arrange
        // TODO: create Trainer with active User
        // TODO: mock trainerRepository.findByUserUsernameAndUserPassword(...)

        // Act
        // TODO: call authService.loginTrainer(...)

        // Assert
        // TODO: assert role TRAINER and correct id/username
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowExceptionWhenTraineeCredentialsAreInvalid() {
        // Arrange
        // TODO: mock repository to return Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> authService.loginTrainee(...))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldThrowExceptionWhenTrainerCredentialsAreInvalid() {
        // Arrange
        // TODO: mock repository to return Optional.empty()

        // Act & Assert
        // TODO: assertThrows(EntityNotFoundException.class, () -> authService.loginTrainer(...))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldRejectLoginWhenTraineeIsInactive() {
        // Arrange
        // TODO: Trainee user.active = false

        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, ...)
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldRejectLoginWhenTrainerIsInactive() {
        // Arrange
        // TODO: Trainer user.active = false

        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, ...)
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldRejectLoginWhenUsernameIsBlank() {
        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, () -> authService.loginTrainee(" ", "pass"))
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldRejectLoginWhenPasswordIsBlank() {
        // Act & Assert
        // TODO: assertThrows(InvalidRequestException.class, () -> authService.loginTrainee("user", " "))
    }
}
