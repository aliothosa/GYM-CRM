package com.elioth.epam.gymcrm.controller;

import com.elioth.epam.gymcrm.auth.AuthSession;
import com.elioth.epam.gymcrm.auth.Role;
import com.elioth.epam.gymcrm.dto.request.CreateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTraineeRequest;
import com.elioth.epam.gymcrm.dto.response.CreatedTraineeResponse;
import com.elioth.epam.gymcrm.dto.response.EmbeddedTrainerResponse;
import com.elioth.epam.gymcrm.dto.response.TraineeResponse;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.exception.InvalidRequestException;
import com.elioth.epam.gymcrm.logging.UserLogger;
import com.elioth.epam.gymcrm.service.TraineeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {

    @Mock
    private TraineeService traineeService;
    @Mock
    private UserLogger userLogger;

    private TraineeController controller;
    private AuthSession session;

    @BeforeEach
    void setUp() {
        controller = new TraineeController(traineeService, userLogger);
        session = new AuthSession(1L, "actor", Role.TRAINEE);
    }

    @Test
    void shouldCreateTrainee() {
        CreateTraineeRequest request = mock(CreateTraineeRequest.class);
        CreatedTraineeResponse body = mock(CreatedTraineeResponse.class);
        when(traineeService.createProfile(request)).thenReturn(body);

        ResponseEntity<CreatedTraineeResponse> response = controller.addTrainee(request);

        assertResponse(response, HttpStatus.CREATED, body);
    }

    @Test
    void shouldRejectInvalidTraineeCreation() {
        CreateTraineeRequest request = mock(CreateTraineeRequest.class);
        when(traineeService.createProfile(request)).thenThrow(new InvalidRequestException("invalid"));

        assertEquals(HttpStatus.BAD_REQUEST, controller.addTrainee(request).getStatusCode());
    }

    @Test
    void shouldGetTraineeOrReturnNotFound() {
        TraineeResponse body = mock(TraineeResponse.class);
        when(traineeService.getProfileByUsername("known")).thenReturn(body);
        when(traineeService.getProfileByUsername("missing"))
                .thenThrow(new EntityNotFoundException("trainee"));

        assertResponse(controller.getTraineeProfile("known", session), HttpStatus.OK, body);
        assertEquals(HttpStatus.NOT_FOUND,
                controller.getTraineeProfile("missing", session).getStatusCode());
    }

    @Test
    void shouldUpdateTrainee() {
        UpdateTraineeRequest request = mock(UpdateTraineeRequest.class);
        TraineeResponse body = mock(TraineeResponse.class);
        when(traineeService.updateProfile("known", request)).thenReturn(body);

        assertResponse(controller.updateTraineeProfile("known", request, session),
                HttpStatus.OK, body);
    }

    @Test
    void shouldMapUpdateFailures() {
        UpdateTraineeRequest missing = mock(UpdateTraineeRequest.class);
        UpdateTraineeRequest invalid = mock(UpdateTraineeRequest.class);
        when(traineeService.updateProfile("missing", missing))
                .thenThrow(new EntityNotFoundException("trainee"));
        when(traineeService.updateProfile("known", invalid))
                .thenThrow(new InvalidRequestException("invalid"));

        assertEquals(HttpStatus.NOT_FOUND,
                controller.updateTraineeProfile("missing", missing, session).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST,
                controller.updateTraineeProfile("known", invalid, session).getStatusCode());
    }

    @Test
    void shouldDeleteTraineeOrReturnNotFound() {
        doNothing().when(traineeService).deleteProfile("known");
        doThrow(new EntityNotFoundException("trainee"))
                .when(traineeService).deleteProfile("missing");

        assertEquals(HttpStatus.NO_CONTENT,
                controller.deleteTraineeProfile("known", session).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND,
                controller.deleteTraineeProfile("missing", session).getStatusCode());
    }

    @Test
    void shouldUpdateTrainerListOrReturnNotFound() {
        Set<String> usernames = Set.of("trainer");
        Set<EmbeddedTrainerResponse> body = Set.of(mock(EmbeddedTrainerResponse.class));
        when(traineeService.updateTrainersToTrainee("known", usernames)).thenReturn(body);
        when(traineeService.updateTrainersToTrainee("missing", usernames))
                .thenThrow(new EntityNotFoundException("trainee"));

        assertResponse(controller.updateTraineeList("known", usernames, session),
                HttpStatus.OK, body);
        assertEquals(HttpStatus.NOT_FOUND,
                controller.updateTraineeList("missing", usernames, session).getStatusCode());
    }

    @Test
    void shouldSetStatusAndMapFailures() {
        doNothing().when(traineeService).setStatus("known", true);
        doThrow(new EntityNotFoundException("trainee"))
                .when(traineeService).setStatus("missing", true);
        doThrow(new InvalidRequestException("invalid"))
                .when(traineeService).setStatus("invalid", true);

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
