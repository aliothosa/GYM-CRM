package com.elioth.epam.gymcrm.auth;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.User;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.exception.InvalidRequestException;
import com.elioth.epam.gymcrm.repository.TraineeRepository;
import com.elioth.epam.gymcrm.repository.TrainerRepository;
import com.elioth.epam.gymcrm.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
    void shouldLoginTraineeWhenCredentialsAreValidAndUserIsActive() {
        Trainee trainee = buildActiveTrainee(1L, "Emily.Davis", "pass123");

        when(traineeRepository.findByUserUsernameAndUserPassword("Emily.Davis", "pass123"))
                .thenReturn(Optional.of(trainee));

        AuthSession session = authService.loginTrainee("Emily.Davis", "pass123");

        assertEquals(Role.TRAINEE, session.role());
        assertEquals("Emily.Davis", session.username());
        assertEquals(1L, session.id());
    }

    @Test
    void shouldLoginTrainerWhenCredentialsAreValidAndUserIsActive() {
        Trainer trainer = buildActiveTrainer(2L, "John.Smith", "pass456");

        when(trainerRepository.findByUserUsernameAndUserPassword("John.Smith", "pass456"))
                .thenReturn(Optional.of(trainer));

        AuthSession session = authService.loginTrainer("John.Smith", "pass456");

        assertEquals(Role.TRAINER, session.role());
        assertEquals("John.Smith", session.username());
        assertEquals(2L, session.id());
    }

    @Test
    void shouldThrowExceptionWhenTraineeCredentialsAreInvalid() {
        when(traineeRepository.findByUserUsernameAndUserPassword("unknown", "wrong"))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> authService.loginTrainee("unknown", "wrong")
        );

        assertEquals("Username or password incorrect", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTrainerCredentialsAreInvalid() {
        when(trainerRepository.findByUserUsernameAndUserPassword("unknown", "wrong"))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> authService.loginTrainer("unknown", "wrong")
        );

        assertEquals("Username or password incorrect", exception.getMessage());
    }

    @Test
    void shouldRejectLoginWhenTraineeIsInactive() {
        Trainee trainee = buildActiveTrainee(1L, "Emily.Davis", "pass123");
        trainee.getUser().setActive(false);

        when(traineeRepository.findByUserUsernameAndUserPassword("Emily.Davis", "pass123"))
                .thenReturn(Optional.of(trainee));

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> authService.loginTrainee("Emily.Davis", "pass123")
        );

        assertEquals("Trainee account is not active", exception.getMessage());
    }

    @Test
    void shouldRejectLoginWhenTrainerIsInactive() {
        Trainer trainer = buildActiveTrainer(2L, "John.Smith", "pass456");
        trainer.getUser().setActive(false);

        when(trainerRepository.findByUserUsernameAndUserPassword("John.Smith", "pass456"))
                .thenReturn(Optional.of(trainer));

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> authService.loginTrainer("John.Smith", "pass456")
        );

        assertEquals("Trainer account is not active", exception.getMessage());
    }

    @Test
    void shouldRejectLoginWhenUsernameIsBlank() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> authService.loginTrainee(" ", "pass")
        );

        assertEquals("Invalid username", exception.getMessage());
    }

    @Test
    void shouldRejectLoginWhenPasswordIsBlank() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> authService.loginTrainee("user", " ")
        );

        assertEquals("Invalid password", exception.getMessage());
    }

    private Trainee buildActiveTrainee(Long traineeId, String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setTraineeId(traineeId);
        trainee.setUser(user);
        return trainee;
    }

    private Trainer buildActiveTrainer(Long trainerId, String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setTrainerId(trainerId);
        trainer.setUser(user);
        return trainer;
    }
}
