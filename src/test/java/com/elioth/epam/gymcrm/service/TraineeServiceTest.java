package com.elioth.epam.gymcrm.service;

import com.elioth.epam.gymcrm.domain.Address;
import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.User;
import com.elioth.epam.gymcrm.dto.request.ChangePasswordRequest;
import com.elioth.epam.gymcrm.dto.request.CreateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTraineeRequest;
import com.elioth.epam.gymcrm.dto.response.CreatedTraineeResponse;
import com.elioth.epam.gymcrm.dto.response.TraineeResponse;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.exception.IncorrectPasswordException;
import com.elioth.epam.gymcrm.exception.InvalidRequestException;
import com.elioth.epam.gymcrm.repository.TraineeRepository;
import com.elioth.epam.gymcrm.repository.TrainerRepository;
import com.elioth.epam.gymcrm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    private static final Long TRAINEE_ID = 1L;
    private static final String USERNAME = "John.Doe";

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
    void shouldCreateProfileWhenRequestIsValid() {
        CreateTraineeRequest request = validCreateRequest();
        when(userRepository.countByFirstNameAndLastName("John", "Doe")).thenReturn(0L);
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(invocation -> {
            Trainee trainee = invocation.getArgument(0);
            trainee.setTraineeId(TRAINEE_ID);
            trainee.getUser().setUserId(10L);
            return trainee;
        });

        CreatedTraineeResponse response = traineeService.createProfile(request);

        assertNotNull(response);
        assertEquals(TRAINEE_ID, response.traineeId());
        assertEquals("John.Doe", response.username());
        assertNotNull(response.password());

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeRepository).save(captor.capture());
        Trainee saved = captor.getValue();
        assertEquals("John", saved.getUser().getFirstName());
        assertEquals("Doe", saved.getUser().getLastName());
        assertEquals("John.Doe", saved.getUser().getUsername());
        assertTrue(saved.getUser().getActive());
        assertEquals(request.birthDate(), saved.getBirthDate());
        assertEquals(request.address(), saved.getAddress());
    }

    @Test
    void shouldThrowInvalidRequestExceptionWhenCreateRequestIsNull() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> traineeService.createProfile(null)
        );
        assertEquals("Request cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowInvalidRequestExceptionWhenCreateRequestHasBlankFirstName() {
        CreateTraineeRequest request = new CreateTraineeRequest(
                " ",
                "Doe",
                LocalDate.of(1990, 1, 1),
                buildAddress()
        );

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> traineeService.createProfile(request)
        );
        assertEquals("First name cannot be empty", exception.getMessage());
    }

    // --- updateProfile ---

    @Test
    void shouldUpdateProfileWhenRequestIsValid() {
        UpdateTraineeRequest request = validUpdateRequest();
        Trainee trainee = buildTrainee(TRAINEE_ID, USERNAME, "John", "Doe", true);
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.of(trainee));

        TraineeResponse response = traineeService.updateProfile(TRAINEE_ID, request);

        assertEquals(TRAINEE_ID, response.traineeId());
        assertEquals("Jane", response.firstName());
        assertEquals("Smith", response.lastName());
        assertEquals(request.birthDate(), response.birthDate());
        assertEquals(request.address(), response.address());
        assertEquals("Jane", trainee.getUser().getFirstName());
        assertEquals("Smith", trainee.getUser().getLastName());
    }

    @Test
    void shouldThrowInvalidRequestExceptionWhenUpdateRequestIsNull() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> traineeService.updateProfile(TRAINEE_ID, null)
        );
        assertEquals("Request cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUpdatingNonExistentTrainee() {
        UpdateTraineeRequest request = validUpdateRequest();
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> traineeService.updateProfile(TRAINEE_ID, request)
        );
        assertEquals("Trainee not found", exception.getMessage());
    }

    // --- deleteProfile ---

    @Test
    void shouldDeleteProfileWhenTraineeExists() {
        Trainee trainee = buildTrainee(TRAINEE_ID, USERNAME, "John", "Doe", true);
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.of(trainee));

        traineeService.deleteProfile(TRAINEE_ID);

        verify(traineeRepository).delete(trainee);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenDeletingNonExistentTrainee() {
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> traineeService.deleteProfile(TRAINEE_ID)
        );
        assertEquals("Trainee not found", exception.getMessage());
    }

    // --- getProfileById ---

    @Test
    void shouldGetProfileByIdWhenTraineeExists() {
        Trainee trainee = buildTrainee(TRAINEE_ID, USERNAME, "John", "Doe", true);
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.of(trainee));

        TraineeResponse response = traineeService.getProfileById(TRAINEE_ID);

        assertEquals(TRAINEE_ID, response.traineeId());
        assertEquals("John", response.firstName());
        assertEquals("Doe", response.lastName());
        assertEquals(USERNAME, response.username());
        assertTrue(response.active());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenGettingProfileByNonExistentId() {
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> traineeService.getProfileById(TRAINEE_ID)
        );
        assertEquals("Trainee not found", exception.getMessage());
    }

    // --- getProfileByUsername ---

    @Test
    void shouldGetProfileByUsernameWhenTraineeExists() {
        Trainee trainee = buildTrainee(TRAINEE_ID, USERNAME, "John", "Doe", true);
        when(traineeRepository.findByUserUsername(USERNAME)).thenReturn(Optional.of(trainee));

        TraineeResponse response = traineeService.getProfileByUsername(USERNAME);

        assertEquals(USERNAME, response.username());
        assertEquals(TRAINEE_ID, response.traineeId());
    }

    @Test
    void shouldThrowInvalidRequestExceptionWhenGettingProfileByBlankUsername() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> traineeService.getProfileByUsername(" ")
        );
        assertEquals("Invalid username", exception.getMessage());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenGettingProfileByUnknownUsername() {
        when(traineeRepository.findByUserUsername(USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> traineeService.getProfileByUsername(USERNAME)
        );
        assertEquals("Trainee not found", exception.getMessage());
    }

    // --- changePassword ---

    @Test
    void shouldChangePasswordWhenOldPasswordIsCorrect() {
        Trainee trainee = buildTrainee(TRAINEE_ID, USERNAME, "John", "Doe", true);
        trainee.getUser().setPassword("oldPass");
        ChangePasswordRequest request = new ChangePasswordRequest("oldPass", "newPass123");
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.of(trainee));

        TraineeResponse response = traineeService.changePassword(TRAINEE_ID, request);

        assertEquals("newPass123", trainee.getUser().getPassword());
        assertEquals(TRAINEE_ID, response.traineeId());
    }

    @Test
    void shouldThrowInvalidRequestExceptionWhenChangePasswordRequestIsNull() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> traineeService.changePassword(TRAINEE_ID, null)
        );
        assertEquals("Invalid request", exception.getMessage());
    }

    @Test
    void shouldThrowIncorrectPasswordExceptionWhenOldPasswordDoesNotMatch() {
        Trainee trainee = buildTrainee(TRAINEE_ID, USERNAME, "John", "Doe", true);
        trainee.getUser().setPassword("oldPass");
        ChangePasswordRequest request = new ChangePasswordRequest("wrongPass", "newPass123");
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.of(trainee));

        IncorrectPasswordException exception = assertThrows(
                IncorrectPasswordException.class,
                () -> traineeService.changePassword(TRAINEE_ID, request)
        );
        assertEquals("Incorrect old password", exception.getMessage());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenChangingPasswordForNonExistentTrainee() {
        ChangePasswordRequest request = new ChangePasswordRequest("oldPass", "newPass123");
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> traineeService.changePassword(TRAINEE_ID, request)
        );
        assertEquals("Trainee not found", exception.getMessage());
    }

    // --- updateTrainersToTrainee ---

    @Test
    void shouldUpdateTrainersToTraineeWhenAllTrainerIdsExist() {
        Trainee trainee = buildTrainee(TRAINEE_ID, USERNAME, "John", "Doe", true);
        trainee.setTrainers(new HashSet<>());
        List<Long> trainerIds = List.of(10L, 20L);
        Trainer trainer1 = buildTrainer(10L, "Trainer.One", "One", "Trainer", true);
        Trainer trainer2 = buildTrainer(20L, "Trainer.Two", "Two", "Trainer", true);
        List<Trainer> trainers = List.of(trainer1, trainer2);
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findAllById(trainerIds)).thenReturn(trainers);

        traineeService.updateTrainersToTrainee(TRAINEE_ID, trainerIds);

        assertEquals(2, trainee.getTrainers().size());
        assertTrue(trainee.getTrainers().containsAll(trainers));
    }

    @Test
    void shouldThrowInvalidRequestExceptionWhenTrainerIdsListIsNull() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> traineeService.updateTrainersToTrainee(TRAINEE_ID, null)
        );
        assertEquals("Trainer list cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenTraineeNotFoundForTrainerUpdate() {
        List<Long> trainerIds = List.of(10L);
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> traineeService.updateTrainersToTrainee(TRAINEE_ID, trainerIds)
        );
        assertEquals("Trainee not found", exception.getMessage());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenOneOrMoreTrainersNotFound() {
        Trainee trainee = buildTrainee(TRAINEE_ID, USERNAME, "John", "Doe", true);
        List<Long> trainerIds = List.of(10L, 20L);
        Trainer trainer1 = buildTrainer(10L, "Trainer.One", "One", "Trainer", true);
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findAllById(trainerIds)).thenReturn(List.of(trainer1));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> traineeService.updateTrainersToTrainee(TRAINEE_ID, trainerIds)
        );
        assertEquals("One or more trainers were not found", exception.getMessage());
    }

    // --- activate ---

    @Test
    void shouldActivateTraineeWhenCurrentlyInactive() {
        Trainee trainee = buildTrainee(TRAINEE_ID, USERNAME, "John", "Doe", false);
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.of(trainee));

        traineeService.activate(TRAINEE_ID);

        assertTrue(trainee.getUser().getActive());
    }

    @Test
    void shouldThrowInvalidRequestExceptionWhenActivatingAlreadyActiveTrainee() {
        Trainee trainee = buildTrainee(TRAINEE_ID, USERNAME, "John", "Doe", true);
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.of(trainee));

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> traineeService.activate(TRAINEE_ID)
        );
        assertEquals("Trainee is already active.", exception.getMessage());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenActivatingNonExistentTrainee() {
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> traineeService.activate(TRAINEE_ID)
        );
        assertEquals("Trainee not found", exception.getMessage());
    }

    // --- deactivate ---

    @Test
    void shouldDeactivateTraineeWhenCurrentlyActive() {
        Trainee trainee = buildTrainee(TRAINEE_ID, USERNAME, "John", "Doe", true);
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.of(trainee));

        traineeService.deactivate(TRAINEE_ID);

        assertFalse(trainee.getUser().getActive());
    }

    @Test
    void shouldThrowInvalidRequestExceptionWhenDeactivatingAlreadyInactiveTrainee() {
        Trainee trainee = buildTrainee(TRAINEE_ID, USERNAME, "John", "Doe", false);
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.of(trainee));

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> traineeService.deactivate(TRAINEE_ID)
        );
        assertEquals("Trainee is already deactivated.", exception.getMessage());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenDeactivatingNonExistentTrainee() {
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> traineeService.deactivate(TRAINEE_ID)
        );
        assertEquals("Trainee not found", exception.getMessage());
    }

    private CreateTraineeRequest validCreateRequest() {
        return new CreateTraineeRequest(
                "John",
                "Doe",
                LocalDate.of(1990, 5, 15),
                buildAddress()
        );
    }

    private UpdateTraineeRequest validUpdateRequest() {
        return new UpdateTraineeRequest(
                "Jane",
                "Smith",
                LocalDate.of(1992, 3, 20),
                new Address("Oak St", "Boston", "MA", "02101", 42)
        );
    }

    private Address buildAddress() {
        return new Address("Main St", "New York", "NY", "10001", 10);
    }

    private Trainee buildTrainee(Long traineeId, String username, String firstName, String lastName, boolean active) {
        User user = buildUser(username, firstName, lastName, "password", active);
        Trainee trainee = new Trainee();
        trainee.setTraineeId(traineeId);
        trainee.setUser(user);
        trainee.setBirthDate(LocalDate.of(1990, 5, 15));
        trainee.setAddress(buildAddress());
        return trainee;
    }

    private Trainer buildTrainer(Long trainerId, String username, String firstName, String lastName, boolean active) {
        User user = buildUser(username, firstName, lastName, "password", active);
        Trainer trainer = new Trainer();
        trainer.setTrainerId(trainerId);
        trainer.setUser(user);
        return trainer;
    }

    private User buildUser(String username, String firstName, String lastName, String password, boolean active) {
        User user = new User();
        user.setUserId(100L);
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
        user.setActive(active);
        return user;
    }
}
