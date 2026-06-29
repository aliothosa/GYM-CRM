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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
    void shouldDelegateCreateTraineeProfileWithoutLogin() {
        CreateTraineeRequest request = new CreateTraineeRequest("John", "Smith", null, null);
        CreatedTraineeResponse response = new CreatedTraineeResponse(10L, "John.Smith", "secret");
        when(traineeService.createProfile(request)).thenReturn(response);

        CreatedTraineeResponse result = facade.createTraineeProfile(request);

        assertEquals(response, result);
        verify(traineeService).createProfile(request);
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
    void shouldLogoutCurrentSession() {
        facade.logout();
        verify(sessionManager).logout();
    }

    // ========================= Practice skeletons (disabled) =========================

    @Test
    @Disabled("Practice skeleton")
    void shouldLoginAsTrainerAndStoreSession() {
        // Arrange
        // TODO: mock authService.loginTrainer(...) -> AuthSession with Role.TRAINER

        // Act
        // TODO: facade.loginAsTrainer(...)

        // Assert
        // TODO: verify sessionManager.login(session)
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldDelegateCreateTrainerProfileWithoutLogin() {
        // Arrange
        // TODO: mock trainerService.createProfile(...)

        // Act & Assert
        // TODO: verify delegation
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldRejectTraineeOperationWhenLoggedAsTrainer() {
        // Arrange
        // TODO: sessionManager.getCurrentSession() -> trainer session

        // Act & Assert
        // TODO: assertThrows(UnauthorizedOperationException.class, () -> facade.getTraineeProfile())
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldRejectTrainerOperationWhenLoggedAsTrainee() {
        // Arrange
        // TODO: session with Role.TRAINEE

        // Act & Assert
        // TODO: assertThrows(UnauthorizedOperationException.class, () -> facade.getTrainerProfile())
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldRejectProtectedOperationWhenNotAuthenticated() {
        // Arrange
        // TODO: sessionManager.getCurrentSession() -> throw UserNotAuthenticatedException

        // Act & Assert
        // TODO: assertThrows(UserNotAuthenticatedException.class, () -> facade.getTraineeProfile())
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldGetTraineeProfileUsingSessionId() {
        // TODO: mock trainee session + traineeService.getProfileById(session.id())
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldRejectGetTraineeProfileByUsernameForAnotherUser() {
        // TODO: session username != requested username -> UnauthorizedOperationException
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldDeleteTraineeProfileAndLogout() {
        // TODO: verify traineeService.deleteProfile + sessionManager.logout
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldChangeTraineePasswordUsingSession() {
        // TODO: verify traineeService.changePassword(session.id(), request)
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldActivateAndDeactivateTraineeProfile() {
        // TODO: verify traineeService.activate/deactivate with session id
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldUpdateTraineeTrainersListUsingSession() {
        // TODO: verify traineeService.updateTrainersToTrainee(session.id(), ids)
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldGetTrainersNotAssignedToCurrentTrainee() {
        // TODO: verify trainerService.getTrainersNotAssignedToTrainee(session.username())
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldGetTraineeTrainingsByCriteriaUsingSessionUsername() {
        // TODO: verify trainingService.getTrainingsByTraineeUsernameAndCriteria(...)
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldGetTrainerProfileUsingSessionId() {
        // TODO: verify trainerService.getProfileById(session.id())
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldUpdateTrainerProfileUsingSession() {
        // TODO: verify trainerService.updateProfile(session.id(), request)
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldFindTrainersBySpecializationWhenAuthenticatedAsTrainer() {
        // TODO: verify trainerService.findBySpecializationName(name)
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldCreateTrainingWhenTrainerIdMatchesSession() {
        // TODO: request.trainerId() == session.id()
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldRejectCreateTrainingWhenTrainerIdDoesNotMatchSession() {
        // TODO: assertThrows(UnauthorizedOperationException.class, ...)
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldUpdateDeleteAndGetTrainingUsingAuthenticatedTrainer() {
        // TODO: verify trainingService methods with session.id()
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldGetTrainerTrainingsByCriteriaUsingSessionUsername() {
        // TODO: verify trainingService.getTrainingsByTrainerUsernameAndCriteria(...)
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldReturnCurrentSessionFromSessionManager() {
        // TODO: verify facade.getCurrentSession() delegates to sessionManager
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldReportAuthenticatedWhenSessionExists() {
        // TODO: when(sessionManager.isAuthenticated()).thenReturn(true)
        // TODO: assertTrue(facade.isAuthenticated())
    }
}
