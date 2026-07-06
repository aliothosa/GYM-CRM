package com.elioth.epam.gymcrm.service;

import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.TrainingType;
import com.elioth.epam.gymcrm.domain.User;
import com.elioth.epam.gymcrm.dto.request.ChangePasswordRequest;
import com.elioth.epam.gymcrm.dto.request.CreateTrainerRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTrainerRequest;
import com.elioth.epam.gymcrm.dto.response.CreatedTrainerResponse;
import com.elioth.epam.gymcrm.dto.response.TrainerResponse;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.exception.IncorrectPasswordException;
import com.elioth.epam.gymcrm.exception.InvalidRequestException;
import com.elioth.epam.gymcrm.repository.TrainerRepository;
import com.elioth.epam.gymcrm.repository.TrainingTypeRepository;
import com.elioth.epam.gymcrm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class TrainerServiceTest {

    private static final Long TRAINER_ID = 1L;
    private static final Long TRAINING_TYPE_ID = 5L;
    private static final String USERNAME = "John.Doe";

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


    @Test
    void shouldCreateProfileWhenRequestIsValid() {
        CreateTrainerRequest request = validCreateRequest();
        TrainingType trainingType = buildTrainingType(TRAINING_TYPE_ID, "YOGA");
        when(userRepository.countByFirstNameAndLastName("John", "Doe")).thenReturn(0L);
        when(trainingTypeRepository.findById(TRAINING_TYPE_ID)).thenReturn(Optional.of(trainingType));
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(invocation -> {
            Trainer trainer = invocation.getArgument(0);
            trainer.setTrainerId(TRAINER_ID);
            trainer.getUser().setUserId(10L);
            return trainer;
        });

        CreatedTrainerResponse response = trainerService.createProfile(request);

        assertNotNull(response);
        assertEquals(TRAINER_ID, response.trainerId());
        assertEquals("John.Doe", response.username());
        assertNotNull(response.password());

        ArgumentCaptor<Trainer> captor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerRepository).save(captor.capture());
        Trainer saved = captor.getValue();
        assertEquals("John", saved.getUser().getFirstName());
        assertEquals("Doe", saved.getUser().getLastName());
        assertEquals("John.Doe", saved.getUser().getUsername());
        assertTrue(saved.getUser().getActive());
        assertEquals(trainingType, saved.getSpecialization());
    }

    @Test
    void shouldThrowInvalidRequestExceptionWhenCreateRequestIsNull() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> trainerService.createProfile(null)
        );
        assertEquals("Request cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenTrainingTypeNotFoundOnCreate() {
        CreateTrainerRequest request = validCreateRequest();
        when(trainingTypeRepository.findById(TRAINING_TYPE_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> trainerService.createProfile(request)
        );
        assertEquals("TrainingType not found", exception.getMessage());
    }


    @Test
    void shouldUpdateProfileWhenRequestIsValid() {
        UpdateTrainerRequest request = validUpdateRequest();
        TrainingType oldType = buildTrainingType(TRAINING_TYPE_ID, "YOGA");
        TrainingType newType = buildTrainingType(6L, "CARDIO");
        Trainer trainer = buildTrainer(TRAINER_ID, USERNAME, "John", "Doe", true, oldType);
        when(trainerRepository.findById(TRAINER_ID)).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findById(6L)).thenReturn(Optional.of(newType));

        TrainerResponse response = trainerService.updateProfile(TRAINER_ID, request);

        assertEquals(TRAINER_ID, response.trainerId());
        assertEquals("Jane", response.firstName());
        assertEquals("Smith", response.lastName());
        assertEquals(6L, response.trainingTypeId());
        assertEquals("CARDIO", response.trainingTypeName());
        assertEquals("Jane", trainer.getUser().getFirstName());
        assertEquals(newType, trainer.getSpecialization());
    }

    @Test
    void shouldThrowInvalidRequestExceptionWhenUpdateRequestIsNull() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> trainerService.updateProfile(TRAINER_ID, null)
        );
        assertEquals("Request cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUpdatingNonExistentTrainer() {
        UpdateTrainerRequest request = validUpdateRequest();
        when(trainerRepository.findById(TRAINER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> trainerService.updateProfile(TRAINER_ID, request)
        );
        assertEquals("Trainer not found", exception.getMessage());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenTrainingTypeNotFoundOnUpdate() {
        UpdateTrainerRequest request = validUpdateRequest();
        Trainer trainer = buildTrainer(TRAINER_ID, USERNAME, "John", "Doe", true, buildTrainingType(TRAINING_TYPE_ID, "YOGA"));
        when(trainerRepository.findById(TRAINER_ID)).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findById(6L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> trainerService.updateProfile(TRAINER_ID, request)
        );
        assertEquals("TrainingType not found", exception.getMessage());
    }


    @Test
    void shouldGetProfileByIdWhenTrainerExists() {
        TrainingType trainingType = buildTrainingType(TRAINING_TYPE_ID, "YOGA");
        Trainer trainer = buildTrainer(TRAINER_ID, USERNAME, "John", "Doe", true, trainingType);
        when(trainerRepository.findById(TRAINER_ID)).thenReturn(Optional.of(trainer));

        TrainerResponse response = trainerService.getProfileById(TRAINER_ID);

        assertEquals(TRAINER_ID, response.trainerId());
        assertEquals("John", response.firstName());
        assertEquals("Doe", response.lastName());
        assertEquals(USERNAME, response.username());
        assertTrue(response.active());
        assertEquals(TRAINING_TYPE_ID, response.trainingTypeId());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenGettingProfileByNonExistentId() {
        when(trainerRepository.findById(TRAINER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> trainerService.getProfileById(TRAINER_ID)
        );
        assertEquals("Trainer not found", exception.getMessage());
    }


    @Test
    void shouldGetProfileByUsernameWhenTrainerExists() {
        TrainingType trainingType = buildTrainingType(TRAINING_TYPE_ID, "YOGA");
        Trainer trainer = buildTrainer(TRAINER_ID, USERNAME, "John", "Doe", true, trainingType);
        when(trainerRepository.findByUserUsername(USERNAME)).thenReturn(Optional.of(trainer));

        TrainerResponse response = trainerService.getProfileByUsername(USERNAME);

        assertEquals(USERNAME, response.username());
        assertEquals(TRAINER_ID, response.trainerId());
    }

    @Test
    void shouldThrowInvalidRequestExceptionWhenGettingProfileByBlankUsername() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> trainerService.getProfileByUsername(" ")
        );
        assertEquals("Invalid username", exception.getMessage());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenGettingProfileByUnknownUsername() {
        when(trainerRepository.findByUserUsername(USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> trainerService.getProfileByUsername(USERNAME)
        );
        assertEquals("Trainer not found", exception.getMessage());
    }


    @Test
    void shouldChangePasswordWhenOldPasswordIsCorrect() {
        TrainingType trainingType = buildTrainingType(TRAINING_TYPE_ID, "YOGA");
        Trainer trainer = buildTrainer(TRAINER_ID, USERNAME, "John", "Doe", true, trainingType);
        trainer.getUser().setPassword("oldPass");
        ChangePasswordRequest request = new ChangePasswordRequest("oldPass", "newPass123");
        when(trainerRepository.findById(TRAINER_ID)).thenReturn(Optional.of(trainer));

        TrainerResponse response = trainerService.changePassword(TRAINER_ID, request);

        assertEquals("newPass123", trainer.getUser().getPassword());
        assertEquals(TRAINER_ID, response.trainerId());
    }

    @Test
    void shouldThrowInvalidRequestExceptionWhenChangePasswordRequestIsNull() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> trainerService.changePassword(TRAINER_ID, null)
        );
        assertEquals("Invalid request", exception.getMessage());
    }

    @Test
    void shouldThrowIncorrectPasswordExceptionWhenOldPasswordDoesNotMatch() {
        TrainingType trainingType = buildTrainingType(TRAINING_TYPE_ID, "YOGA");
        Trainer trainer = buildTrainer(TRAINER_ID, USERNAME, "John", "Doe", true, trainingType);
        trainer.getUser().setPassword("oldPass");
        ChangePasswordRequest request = new ChangePasswordRequest("wrongPass", "newPass123");
        when(trainerRepository.findById(TRAINER_ID)).thenReturn(Optional.of(trainer));

        IncorrectPasswordException exception = assertThrows(
                IncorrectPasswordException.class,
                () -> trainerService.changePassword(TRAINER_ID, request)
        );
        assertEquals("Incorrect old password", exception.getMessage());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenChangingPasswordForNonExistentTrainer() {
        ChangePasswordRequest request = new ChangePasswordRequest("oldPass", "newPass123");
        when(trainerRepository.findById(TRAINER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> trainerService.changePassword(TRAINER_ID, request)
        );
        assertEquals("Trainer not found", exception.getMessage());
    }


    @Test
    void shouldFindTrainersBySpecializationNameWhenTrainersExist() {
        String specializationName = "Yoga";
        TrainingType trainingType = buildTrainingType(TRAINING_TYPE_ID, "YOGA");
        Trainer trainer1 = buildTrainer(1L, "Trainer.One", "One", "Trainer", true, trainingType);
        Trainer trainer2 = buildTrainer(2L, "Trainer.Two", "Two", "Trainer", true, trainingType);
        when(trainerRepository.findAllBySpecializationName("YOGA")).thenReturn(List.of(trainer1, trainer2));

        List<TrainerResponse> responses = trainerService.findBySpecializationName(specializationName);

        assertEquals(2, responses.size());
        assertEquals("YOGA", responses.get(0).trainingTypeName());
        verify(trainerRepository).findAllBySpecializationName("YOGA");
    }

    @Test
    void shouldThrowInvalidRequestExceptionWhenSpecializationNameIsBlank() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> trainerService.findBySpecializationName(" ")
        );
        assertEquals("Specialization name cannot be empty", exception.getMessage());
    }


    @Test
    void shouldGetTrainersNotAssignedToTraineeWhenTraineeUsernameIsValid() {
        String traineeUsername = "Emily.Davis";
        TrainingType trainingType = buildTrainingType(TRAINING_TYPE_ID, "YOGA");
        Trainer trainer = buildTrainer(TRAINER_ID, "John.Doe", "John", "Doe", true, trainingType);
        when(trainerRepository.findTrainersNotAssignedToTrainee(traineeUsername)).thenReturn(List.of(trainer));

        List<TrainerResponse> responses = trainerService.getTrainersNotAssignedToTrainee(traineeUsername);

        assertEquals(1, responses.size());
        assertEquals(TRAINER_ID, responses.get(0).trainerId());
        verify(trainerRepository).findTrainersNotAssignedToTrainee(traineeUsername);
    }

    @Test
    void shouldThrowInvalidRequestExceptionWhenTraineeUsernameIsBlank() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> trainerService.getTrainersNotAssignedToTrainee(" ")
        );
        assertEquals("Invalid trainee username", exception.getMessage());
    }


    @Test
    void shouldActivateTrainerWhenCurrentlyInactive() {
        TrainingType trainingType = buildTrainingType(TRAINING_TYPE_ID, "YOGA");
        Trainer trainer = buildTrainer(TRAINER_ID, USERNAME, "John", "Doe", false, trainingType);
        when(trainerRepository.findById(TRAINER_ID)).thenReturn(Optional.of(trainer));

        trainerService.activate(TRAINER_ID);

        assertTrue(trainer.getUser().getActive());
    }

    @Test
    void shouldThrowInvalidRequestExceptionWhenActivatingAlreadyActiveTrainer() {
        TrainingType trainingType = buildTrainingType(TRAINING_TYPE_ID, "YOGA");
        Trainer trainer = buildTrainer(TRAINER_ID, USERNAME, "John", "Doe", true, trainingType);
        when(trainerRepository.findById(TRAINER_ID)).thenReturn(Optional.of(trainer));

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> trainerService.activate(TRAINER_ID)
        );
        assertEquals("Trainer is already active.", exception.getMessage());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenActivatingNonExistentTrainer() {
        when(trainerRepository.findById(TRAINER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> trainerService.activate(TRAINER_ID)
        );
        assertEquals("Trainer not found", exception.getMessage());
    }


    @Test
    void shouldDeactivateTrainerWhenCurrentlyActive() {
        TrainingType trainingType = buildTrainingType(TRAINING_TYPE_ID, "YOGA");
        Trainer trainer = buildTrainer(TRAINER_ID, USERNAME, "John", "Doe", true, trainingType);
        when(trainerRepository.findById(TRAINER_ID)).thenReturn(Optional.of(trainer));

        trainerService.deactivate(TRAINER_ID);

        assertFalse(trainer.getUser().getActive());
    }

    @Test
    void shouldThrowInvalidRequestExceptionWhenDeactivatingAlreadyInactiveTrainer() {
        TrainingType trainingType = buildTrainingType(TRAINING_TYPE_ID, "YOGA");
        Trainer trainer = buildTrainer(TRAINER_ID, USERNAME, "John", "Doe", false, trainingType);
        when(trainerRepository.findById(TRAINER_ID)).thenReturn(Optional.of(trainer));

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> trainerService.deactivate(TRAINER_ID)
        );
        assertEquals("Trainer is already deactivated.", exception.getMessage());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenDeactivatingNonExistentTrainer() {
        when(trainerRepository.findById(TRAINER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> trainerService.deactivate(TRAINER_ID)
        );
        assertEquals("Trainer not found", exception.getMessage());
    }

    private CreateTrainerRequest validCreateRequest() {
        return new CreateTrainerRequest("John", "Doe", TRAINING_TYPE_ID);
    }

    private UpdateTrainerRequest validUpdateRequest() {
        return new UpdateTrainerRequest("Jane", "Smith", 6L);
    }

    private TrainingType buildTrainingType(Long id, String name) {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(id);
        trainingType.setName(name);
        return trainingType;
    }

    private Trainer buildTrainer(
            Long trainerId,
            String username,
            String firstName,
            String lastName,
            boolean active,
            TrainingType specialization
    ) {
        User user = buildUser(username, firstName, lastName, "password", active);
        Trainer trainer = new Trainer();
        trainer.setTrainerId(trainerId);
        trainer.setUser(user);
        trainer.setSpecialization(specialization);
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
