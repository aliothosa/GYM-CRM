package com.elioth.epam.gymcrm.controller;

import com.elioth.epam.gymcrm.dto.request.CreateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTraineeRequest;
import com.elioth.epam.gymcrm.dto.response.CreatedTraineeResponse;
import com.elioth.epam.gymcrm.dto.response.EmbeddedTrainerResponse;
import com.elioth.epam.gymcrm.dto.response.TraineeResponse;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.exception.InvalidRequestException;
import com.elioth.epam.gymcrm.logging.UserLogger;
import com.elioth.epam.gymcrm.metrics.GymCrmMetrics;
import com.elioth.epam.gymcrm.service.TraineeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/** Original controller behavior, adapted from AuthSession to Spring Security Authentication. */
@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {
    @Mock private TraineeService traineeService; @Mock private UserLogger userLogger; @Mock private GymCrmMetrics metrics; @Mock private Authentication authentication;
    private TraineeController controller;
    @BeforeEach void setUp() { controller = new TraineeController(traineeService, userLogger, metrics); lenient().when(authentication.getName()).thenReturn("actor"); }

    @Test void createsTraineeAndRejectsInvalidRequest() {
        CreateTraineeRequest request = mock(CreateTraineeRequest.class); CreatedTraineeResponse body = mock(CreatedTraineeResponse.class);
        when(traineeService.createProfile(request)).thenReturn(body);
        assertEquals(201, controller.addTrainee(request).getStatusCode().value());
        when(traineeService.createProfile(request)).thenThrow(new InvalidRequestException("invalid"));
        assertEquals(400, controller.addTrainee(request).getStatusCode().value());
    }

    @Test void mapsProfileAndMutationFailures() {
        TraineeResponse body = mock(TraineeResponse.class); UpdateTraineeRequest update = mock(UpdateTraineeRequest.class);
        when(traineeService.getProfileByUsername("known")).thenReturn(body); when(traineeService.getProfileByUsername("missing")).thenThrow(new EntityNotFoundException("trainee"));
        assertEquals(200, controller.getTraineeProfile("known").getStatusCode().value()); assertEquals(404, controller.getTraineeProfile("missing").getStatusCode().value());
        when(traineeService.updateProfile("missing", update)).thenThrow(new EntityNotFoundException("trainee")); when(traineeService.updateProfile("bad", update)).thenThrow(new InvalidRequestException("invalid"));
        assertEquals(404, controller.updateTraineeProfile("missing", update, authentication).getStatusCode().value()); assertEquals(400, controller.updateTraineeProfile("bad", update, authentication).getStatusCode().value());
        doNothing().when(traineeService).deleteProfile("known"); doThrow(new EntityNotFoundException("trainee")).when(traineeService).deleteProfile("missing");
        assertEquals(204, controller.deleteTraineeProfile("known", authentication).getStatusCode().value()); assertEquals(404, controller.deleteTraineeProfile("missing", authentication).getStatusCode().value());
    }

    @Test void updatesTrainerListAndStatus() {
        Set<String> usernames = Set.of("trainer"); when(traineeService.updateTrainersToTrainee("known", usernames)).thenReturn(Set.of()); when(traineeService.updateTrainersToTrainee("missing", usernames)).thenThrow(new EntityNotFoundException("trainee"));
        assertEquals(200, controller.updateTraineeList("known", usernames, authentication).getStatusCode().value()); assertEquals(404, controller.updateTraineeList("missing", usernames, authentication).getStatusCode().value());
        doNothing().when(traineeService).setStatus("known", true); doThrow(new EntityNotFoundException("trainee")).when(traineeService).setStatus("missing", true); doThrow(new InvalidRequestException("invalid")).when(traineeService).setStatus("bad", true);
        assertEquals(200, controller.setStatus("known", true, authentication).getStatusCode().value()); assertEquals(404, controller.setStatus("missing", true, authentication).getStatusCode().value()); assertEquals(400, controller.setStatus("bad", true, authentication).getStatusCode().value());
    }
}
