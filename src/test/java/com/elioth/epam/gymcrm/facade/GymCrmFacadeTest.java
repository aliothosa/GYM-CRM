package com.elioth.epam.gymcrm.facade;

import com.elioth.epam.gymcrm.auth.AuthSession;
import com.elioth.epam.gymcrm.auth.Role;
import com.elioth.epam.gymcrm.auth.SessionManager;
import com.elioth.epam.gymcrm.dto.request.CreateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTraineeRequest;
import com.elioth.epam.gymcrm.dto.response.CreatedTraineeResponse;
import com.elioth.epam.gymcrm.dto.response.TraineeResponse;
import com.elioth.epam.gymcrm.service.AuthService;
import com.elioth.epam.gymcrm.service.TraineeService;
import com.elioth.epam.gymcrm.service.TrainerService;
import com.elioth.epam.gymcrm.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
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
}
