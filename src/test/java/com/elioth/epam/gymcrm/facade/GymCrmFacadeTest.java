package com.elioth.epam.gymcrm.facade;

import com.elioth.epam.gymcrm.auth.AuthSession;
import com.elioth.epam.gymcrm.auth.Role;
import com.elioth.epam.gymcrm.auth.SessionManager;
import com.elioth.epam.gymcrm.dto.request.ChangePasswordRequest;
import com.elioth.epam.gymcrm.dto.request.CreateTrainerRequest;
import com.elioth.epam.gymcrm.dto.request.CreateTrainingRequest;
import com.elioth.epam.gymcrm.dto.request.CreateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTrainerRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTrainingRequest;
import com.elioth.epam.gymcrm.dto.response.CreatedTrainerResponse;
import com.elioth.epam.gymcrm.dto.response.CreatedTraineeResponse;
import com.elioth.epam.gymcrm.dto.response.TrainerResponse;
import com.elioth.epam.gymcrm.dto.response.TraineeResponse;
import com.elioth.epam.gymcrm.dto.response.TrainingResponse;
import com.elioth.epam.gymcrm.exception.UnauthorizedOperationException;
import com.elioth.epam.gymcrm.exception.UserNotAuthenticatedException;
import com.elioth.epam.gymcrm.service.AuthService;
import com.elioth.epam.gymcrm.service.TraineeService;
import com.elioth.epam.gymcrm.service.TrainerService;
import com.elioth.epam.gymcrm.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymCrmFacadeTest {

    @Mock
    private AuthService authService;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    private GymCrmFacade facade;

    @BeforeEach
    void setUp() {
        facade = new GymCrmFacade(
                authService,
                sessionManager,
                traineeService,
                trainerService,
                trainingService
        );
    }

    @Test
    void shouldLoginAsTraineeAndStoreSession() {
        AuthSession session = new AuthSession(1L, "Emily.Davis", Role.TRAINEE);
        when(authService.loginTrainee("Emily.Davis", "pass123")).thenReturn(session);

        AuthSession result = facade.loginAsTrainee("Emily.Davis", "pass123");

        assertEquals(session, result);
        verify(sessionManager).login(session);
    }

    @Test
    void shouldLoginAsTrainerAndStoreSession() {
        AuthSession session = new AuthSession(2L, "Jane.Trainer", Role.TRAINER);
        when(authService.loginTrainer("Jane.Trainer", "pass456")).thenReturn(session);

        AuthSession result = facade.loginAsTrainer("Jane.Trainer", "pass456");

        assertEquals(session, result);
        verify(sessionManager).login(session);
    }

    @Test
    void shouldDelegateCreateTraineeProfileWithoutLogin() {
        CreateTraineeRequest request = new CreateTraineeRequest("John", "Smith", null, null);
        CreatedTraineeResponse response = new CreatedTraineeResponse(10L, "John.Smith", "secret");
        when(traineeService.createProfile(request)).thenReturn(response);

        CreatedTraineeResponse result = facade.createTraineeProfile(request);

        assertEquals(response, result);
        verify(traineeService).createProfile(request);
    }

    @Test
    void shouldDelegateCreateTrainerProfileWithoutLogin() {
        CreateTrainerRequest request = new CreateTrainerRequest("Jane", "Trainer", 1L);
        CreatedTrainerResponse response = new CreatedTrainerResponse(20L, "Jane.Trainer", "secret");
        when(trainerService.createProfile(request)).thenReturn(response);

        CreatedTrainerResponse result = facade.createTrainerProfile(request);

        assertEquals(response, result);
        verify(trainerService).createProfile(request);
    }

    @Test
    void shouldRejectTraineeOperationWhenLoggedAsTrainer() {
        AuthSession trainerSession = new AuthSession(2L, "Jane.Trainer", Role.TRAINER);
        when(sessionManager.getCurrentSession()).thenReturn(trainerSession);

        assertThrows(UnauthorizedOperationException.class, () -> facade.getTraineeProfile());
    }

    @Test
    void shouldRejectTrainerOperationWhenLoggedAsTrainee() {
        AuthSession traineeSession = new AuthSession(1L, "Emily.Davis", Role.TRAINEE);
        when(sessionManager.getCurrentSession()).thenReturn(traineeSession);

        assertThrows(UnauthorizedOperationException.class, () -> facade.getTrainerProfile());
    }

    @Test
    void shouldRejectProtectedOperationWhenNotAuthenticated() {
        when(sessionManager.getCurrentSession())
                .thenThrow(new UserNotAuthenticatedException("No user is logged in"));

        assertThrows(UserNotAuthenticatedException.class, () -> facade.getTraineeProfile());
    }

    @Test
    void shouldUseCurrentTraineeSessionForProfileUpdate() {
        AuthSession session = new AuthSession(1L, "Emily.Davis", Role.TRAINEE);
        UpdateTraineeRequest request = new UpdateTraineeRequest("Emily", "Davis", null, null);
        TraineeResponse response = new TraineeResponse(1L, 4L, "Emily", "Davis", "Emily.Davis", true, null, null);

        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(traineeService.updateProfile(eq(1L), eq(request))).thenReturn(response);

        TraineeResponse result = facade.updateTraineeProfile(request);

        assertEquals(response, result);
        verify(traineeService).updateProfile(1L, request);
    }

    @Test
    void shouldGetTraineeProfileUsingSessionId() {
        AuthSession session = new AuthSession(1L, "Emily.Davis", Role.TRAINEE);
        TraineeResponse response = new TraineeResponse(1L, 4L, "Emily", "Davis", "Emily.Davis", true, null, null);

        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(traineeService.getProfileById(1L)).thenReturn(response);

        TraineeResponse result = facade.getTraineeProfile();

        assertEquals(response, result);
        verify(traineeService).getProfileById(1L);
    }

    @Test
    void shouldRejectGetTraineeProfileByUsernameForAnotherUser() {
        AuthSession session = new AuthSession(1L, "Emily.Davis", Role.TRAINEE);
        when(sessionManager.getCurrentSession()).thenReturn(session);

        assertThrows(UnauthorizedOperationException.class,
                () -> facade.getTraineeProfileByUsername("Other.User"));
    }

    @Test
    void shouldGetTraineeProfileByUsernameWhenSessionMatches() {
        AuthSession session = new AuthSession(1L, "Emily.Davis", Role.TRAINEE);
        TraineeResponse response = new TraineeResponse(1L, 4L, "Emily", "Davis", "Emily.Davis", true, null, null);

        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(traineeService.getProfileByUsername("Emily.Davis")).thenReturn(response);

        TraineeResponse result = facade.getTraineeProfileByUsername("Emily.Davis");

        assertEquals(response, result);
        verify(traineeService).getProfileByUsername("Emily.Davis");
    }

    @Test
    void shouldDeleteTraineeProfileAndLogout() {
        AuthSession session = new AuthSession(1L, "Emily.Davis", Role.TRAINEE);
        when(sessionManager.getCurrentSession()).thenReturn(session);

        facade.deleteTraineeProfile();

        verify(traineeService).deleteProfile(1L);
        verify(sessionManager).logout();
    }

    @Test
    void shouldChangeTraineePasswordUsingSession() {
        AuthSession session = new AuthSession(1L, "Emily.Davis", Role.TRAINEE);
        ChangePasswordRequest request = new ChangePasswordRequest("oldPass", "newPass");
        TraineeResponse response = new TraineeResponse(1L, 4L, "Emily", "Davis", "Emily.Davis", true, null, null);

        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(traineeService.changePassword(1L, request)).thenReturn(response);

        TraineeResponse result = facade.changeTraineePassword(request);

        assertEquals(response, result);
        verify(traineeService).changePassword(1L, request);
    }

    @Test
    void shouldActivateAndDeactivateTraineeProfile() {
        AuthSession session = new AuthSession(1L, "Emily.Davis", Role.TRAINEE);
        when(sessionManager.getCurrentSession()).thenReturn(session);

        facade.activateTraineeProfile();
        verify(traineeService).activate(1L);

        facade.deactivateTraineeProfile();
        verify(traineeService).deactivate(1L);
    }

    @Test
    void shouldUpdateTraineeTrainersListUsingSession() {
        AuthSession session = new AuthSession(1L, "Emily.Davis", Role.TRAINEE);
        List<Long> trainerIds = List.of(10L, 11L);
        when(sessionManager.getCurrentSession()).thenReturn(session);

        facade.updateTraineeTrainersList(trainerIds);

        verify(traineeService).updateTrainersToTrainee(1L, trainerIds);
    }

    @Test
    void shouldGetTrainersNotAssignedToCurrentTrainee() {
        AuthSession session = new AuthSession(1L, "Emily.Davis", Role.TRAINEE);
        TrainerResponse trainer = new TrainerResponse(10L, 20L, "Bob", "Lee", "Bob.Lee", true, 1L, "Yoga");
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(trainerService.getTrainersNotAssignedToTrainee("Emily.Davis")).thenReturn(List.of(trainer));

        List<TrainerResponse> result = facade.getTrainersNotAssignedToTrainee();

        assertEquals(1, result.size());
        assertEquals(trainer, result.getFirst());
        verify(trainerService).getTrainersNotAssignedToTrainee("Emily.Davis");
    }

    @Test
    void shouldGetTraineeTrainingsByCriteriaUsingSessionUsername() {
        AuthSession session = new AuthSession(1L, "Emily.Davis", Role.TRAINEE);
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);
        TrainingResponse training = new TrainingResponse(
                1L, 1L, "Emily.Davis", "Emily", "Davis",
                2L, "Jane.Trainer", "Jane", "Trainer",
                3L, "Yoga", "Morning Yoga", LocalDate.of(2024, 6, 1), 60L
        );

        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(trainingService.getTrainingsByTraineeUsernameAndCriteria(
                "Emily.Davis", from, to, "Jane", "Yoga"
        )).thenReturn(List.of(training));

        List<TrainingResponse> result = facade.getTraineeTrainingsByCriteria(from, to, "Jane", "Yoga");

        assertEquals(1, result.size());
        assertEquals(training, result.getFirst());
        verify(trainingService).getTrainingsByTraineeUsernameAndCriteria(
                "Emily.Davis", from, to, "Jane", "Yoga"
        );
    }

    @Test
    void shouldGetTrainerProfileUsingSessionId() {
        AuthSession session = new AuthSession(2L, "Jane.Trainer", Role.TRAINER);
        TrainerResponse response = new TrainerResponse(2L, 5L, "Jane", "Trainer", "Jane.Trainer", true, 1L, "Yoga");

        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(trainerService.getProfileById(2L)).thenReturn(response);

        TrainerResponse result = facade.getTrainerProfile();

        assertEquals(response, result);
        verify(trainerService).getProfileById(2L);
    }

    @Test
    void shouldGetTrainerProfileByUsernameWhenSessionMatches() {
        AuthSession session = new AuthSession(2L, "Jane.Trainer", Role.TRAINER);
        TrainerResponse response = new TrainerResponse(2L, 5L, "Jane", "Trainer", "Jane.Trainer", true, 1L, "Yoga");

        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(trainerService.getProfileByUsername("Jane.Trainer")).thenReturn(response);

        TrainerResponse result = facade.getTrainerProfileByUsername("Jane.Trainer");

        assertEquals(response, result);
        verify(trainerService).getProfileByUsername("Jane.Trainer");
    }

    @Test
    void shouldRejectGetTrainerProfileByUsernameForAnotherUser() {
        AuthSession session = new AuthSession(2L, "Jane.Trainer", Role.TRAINER);
        when(sessionManager.getCurrentSession()).thenReturn(session);

        assertThrows(UnauthorizedOperationException.class,
                () -> facade.getTrainerProfileByUsername("Other.Trainer"));
    }

    @Test
    void shouldUpdateTrainerProfileUsingSession() {
        AuthSession session = new AuthSession(2L, "Jane.Trainer", Role.TRAINER);
        UpdateTrainerRequest request = new UpdateTrainerRequest("Jane", "Smith", 1L);
        TrainerResponse response = new TrainerResponse(2L, 5L, "Jane", "Smith", "Jane.Trainer", true, 1L, "Yoga");

        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(trainerService.updateProfile(2L, request)).thenReturn(response);

        TrainerResponse result = facade.updateTrainerProfile(request);

        assertEquals(response, result);
        verify(trainerService).updateProfile(2L, request);
    }

    @Test
    void shouldChangeTrainerPasswordUsingSession() {
        AuthSession session = new AuthSession(2L, "Jane.Trainer", Role.TRAINER);
        ChangePasswordRequest request = new ChangePasswordRequest("oldPass", "newPass");
        TrainerResponse response = new TrainerResponse(2L, 5L, "Jane", "Trainer", "Jane.Trainer", true, 1L, "Yoga");

        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(trainerService.changePassword(2L, request)).thenReturn(response);

        TrainerResponse result = facade.changeTrainerPassword(request);

        assertEquals(response, result);
        verify(trainerService).changePassword(2L, request);
    }

    @Test
    void shouldActivateAndDeactivateTrainerProfile() {
        AuthSession session = new AuthSession(2L, "Jane.Trainer", Role.TRAINER);
        when(sessionManager.getCurrentSession()).thenReturn(session);

        facade.activateTrainerProfile();
        verify(trainerService).activate(2L);

        facade.deactivateTrainerProfile();
        verify(trainerService).deactivate(2L);
    }

    @Test
    void shouldFindTrainersBySpecializationWhenAuthenticatedAsTrainer() {
        AuthSession session = new AuthSession(2L, "Jane.Trainer", Role.TRAINER);
        TrainerResponse trainer = new TrainerResponse(2L, 5L, "Jane", "Trainer", "Jane.Trainer", true, 1L, "Yoga");

        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(trainerService.findBySpecializationName("Yoga")).thenReturn(List.of(trainer));

        List<TrainerResponse> result = facade.findTrainersBySpecializationName("Yoga");

        assertEquals(1, result.size());
        assertEquals(trainer, result.getFirst());
        verify(trainerService).findBySpecializationName("Yoga");
    }

    @Test
    void shouldCreateTrainingWhenTrainerIdMatchesSession() {
        AuthSession session = new AuthSession(2L, "Jane.Trainer", Role.TRAINER);
        CreateTrainingRequest request = new CreateTrainingRequest(
                1L, 2L, 3L, "Morning Yoga", LocalDate.of(2024, 6, 1), 60L
        );
        TrainingResponse response = new TrainingResponse(
                100L, 1L, "Emily.Davis", "Emily", "Davis",
                2L, "Jane.Trainer", "Jane", "Trainer",
                3L, "Yoga", "Morning Yoga", LocalDate.of(2024, 6, 1), 60L
        );

        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(trainingService.createTraining(2L, request)).thenReturn(response);

        TrainingResponse result = facade.createTraining(request);

        assertEquals(response, result);
        verify(trainingService).createTraining(2L, request);
    }

    @Test
    void shouldRejectCreateTrainingWhenTrainerIdDoesNotMatchSession() {
        AuthSession session = new AuthSession(2L, "Jane.Trainer", Role.TRAINER);
        CreateTrainingRequest request = new CreateTrainingRequest(
                1L, 99L, 3L, "Morning Yoga", LocalDate.of(2024, 6, 1), 60L
        );
        when(sessionManager.getCurrentSession()).thenReturn(session);

        assertThrows(UnauthorizedOperationException.class, () -> facade.createTraining(request));
    }

    @Test
    void shouldUpdateDeleteAndGetTrainingUsingAuthenticatedTrainer() {
        AuthSession session = new AuthSession(2L, "Jane.Trainer", Role.TRAINER);
        UpdateTrainingRequest updateRequest = new UpdateTrainingRequest(
                "Evening Yoga", LocalDate.of(2024, 6, 2), 90L
        );
        TrainingResponse response = new TrainingResponse(
                100L, 1L, "Emily.Davis", "Emily", "Davis",
                2L, "Jane.Trainer", "Jane", "Trainer",
                3L, "Yoga", "Evening Yoga", LocalDate.of(2024, 6, 2), 90L
        );

        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(trainingService.updateTraining(2L, 100L, updateRequest)).thenReturn(response);
        when(trainingService.getTrainingById(2L, 100L)).thenReturn(response);

        TrainingResponse updated = facade.updateTraining(100L, updateRequest);
        assertEquals(response, updated);
        verify(trainingService).updateTraining(2L, 100L, updateRequest);

        facade.deleteTraining(100L);
        verify(trainingService).deleteTraining(2L, 100L);

        TrainingResponse fetched = facade.getTrainingById(100L);
        assertEquals(response, fetched);
        verify(trainingService).getTrainingById(2L, 100L);
    }

    @Test
    void shouldGetTrainerTrainingsByCriteriaUsingSessionUsername() {
        AuthSession session = new AuthSession(2L, "Jane.Trainer", Role.TRAINER);
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);
        TrainingResponse training = new TrainingResponse(
                100L, 1L, "Emily.Davis", "Emily", "Davis",
                2L, "Jane.Trainer", "Jane", "Trainer",
                3L, "Yoga", "Morning Yoga", LocalDate.of(2024, 6, 1), 60L
        );

        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(trainingService.getTrainingsByTrainerUsernameAndCriteria(
                "Jane.Trainer", from, to, "Emily"
        )).thenReturn(List.of(training));

        List<TrainingResponse> result = facade.getTrainerTrainingsByCriteria(from, to, "Emily");

        assertEquals(1, result.size());
        assertEquals(training, result.getFirst());
        verify(trainingService).getTrainingsByTrainerUsernameAndCriteria(
                "Jane.Trainer", from, to, "Emily"
        );
    }

    @Test
    void shouldReturnCurrentSessionFromSessionManager() {
        AuthSession session = new AuthSession(1L, "Emily.Davis", Role.TRAINEE);
        when(sessionManager.getCurrentSession()).thenReturn(session);

        AuthSession result = facade.getCurrentSession();

        assertEquals(session, result);
    }

    @Test
    void shouldReportAuthenticatedWhenSessionExists() {
        when(sessionManager.isAuthenticated()).thenReturn(true);

        assertTrue(facade.isAuthenticated());
    }

    @Test
    void shouldLogoutCurrentSession() {
        facade.logout();
        verify(sessionManager).logout();
    }
}
