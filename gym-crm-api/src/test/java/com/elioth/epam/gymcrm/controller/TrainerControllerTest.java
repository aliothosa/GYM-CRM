package com.elioth.epam.gymcrm.controller;

import com.elioth.epam.gymcrm.auth.AuthSession;
import com.elioth.epam.gymcrm.auth.Role;
import com.elioth.epam.gymcrm.dto.request.CreateTrainerRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTrainerRequest;
import com.elioth.epam.gymcrm.dto.response.CreatedTrainerResponse;
import com.elioth.epam.gymcrm.dto.response.EmbeddedTrainerResponse;
import com.elioth.epam.gymcrm.dto.response.TrainerResponse;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.exception.InvalidRequestException;
import com.elioth.epam.gymcrm.logging.UserLogger;
import com.elioth.epam.gymcrm.metrics.GymCrmMetrics;
import com.elioth.epam.gymcrm.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {

    @Mock
    private TrainerService trainerService;
    @Mock
    private UserLogger userLogger;
    @Mock
    private GymCrmMetrics metrics;

    private TrainerController controller;
    private AuthSession session;

    @BeforeEach
    void setUp() {
        controller = new TrainerController(trainerService, userLogger, metrics);
        session = new AuthSession(1L, "actor", Role.TRAINER);
    }

    @Test
    void shouldCreateTrainer() {
        CreateTrainerRequest request = mock(CreateTrainerRequest.class);
        CreatedTrainerResponse body = mock(CreatedTrainerResponse.class);
        when(trainerService.createProfile(request)).thenReturn(body);

        assertResponse(controller.createTrainer(request), HttpStatus.CREATED, body);
    }

    @Test
    void shouldGetTrainerOrReturnNotFound() {
        TrainerResponse body = mock(TrainerResponse.class);
        when(trainerService.getProfileByUsername("known")).thenReturn(body);
        when(trainerService.getProfileByUsername("missing"))
                .thenThrow(new EntityNotFoundException("trainer"));

        assertResponse(controller.getTrainer("known", session), HttpStatus.OK, body);
        assertEquals(HttpStatus.NOT_FOUND,
                controller.getTrainer("missing", session).getStatusCode());
    }

    @Test
    void shouldUpdateTrainer() {
        UpdateTrainerRequest request = mock(UpdateTrainerRequest.class);
        TrainerResponse body = mock(TrainerResponse.class);
        when(trainerService.updateProfile("known", request)).thenReturn(body);

        assertResponse(controller.updateTrainer("known", request, session), HttpStatus.OK, body);
    }

    @Test
    void shouldMapUpdateFailures() {
        UpdateTrainerRequest missing = mock(UpdateTrainerRequest.class);
        UpdateTrainerRequest invalid = mock(UpdateTrainerRequest.class);
        when(trainerService.updateProfile("missing", missing))
                .thenThrow(new EntityNotFoundException("trainer"));
        when(trainerService.updateProfile("known", invalid))
                .thenThrow(new InvalidRequestException("invalid"));

        assertEquals(HttpStatus.NOT_FOUND,
                controller.updateTrainer("missing", missing, session).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST,
                controller.updateTrainer("known", invalid, session).getStatusCode());
    }

    @Test
    void shouldGetUnassignedTrainersOrReturnNotFound() {
        List<EmbeddedTrainerResponse> body = List.of(mock(EmbeddedTrainerResponse.class));
        when(trainerService.getTrainersNotAssignedToTraineeEmbedded("known")).thenReturn(body);
        when(trainerService.getTrainersNotAssignedToTraineeEmbedded("missing"))
                .thenThrow(new EntityNotFoundException("trainee"));

        assertResponse(controller.getAllTrainersNotAssignedToTrainee("known", session),
                HttpStatus.OK, body);
        assertEquals(HttpStatus.NOT_FOUND,
                controller.getAllTrainersNotAssignedToTrainee("missing", session).getStatusCode());
    }

    @Test
    void shouldSetStatusAndMapFailures() {
        doNothing().when(trainerService).setStatus("known", true);
        doThrow(new EntityNotFoundException("trainer"))
                .when(trainerService).setStatus("missing", true);
        doThrow(new InvalidRequestException("invalid"))
                .when(trainerService).setStatus("invalid", true);

        assertEquals(HttpStatus.OK, controller.setStatus("known", true, session).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND,
                controller.setStatus("missing", true, session).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST,
                controller.setStatus("invalid", true, session).getStatusCode());
    }

    private void assertResponse(ResponseEntity<?> response, HttpStatus status, Object body) {
        assertEquals(status, response.getStatusCode());
        assertSame(body, response.getBody());
    }
}
