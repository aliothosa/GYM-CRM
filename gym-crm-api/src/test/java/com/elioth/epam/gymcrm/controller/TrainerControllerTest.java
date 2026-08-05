package com.elioth.epam.gymcrm.controller;

import com.elioth.epam.gymcrm.dto.request.CreateTrainerRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTrainerRequest;
import com.elioth.epam.gymcrm.dto.response.CreatedTrainerResponse;
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
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/** Original trainer controller assertions adapted to stateless Authentication arguments. */
@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {
    @Mock private TrainerService trainerService; @Mock private UserLogger userLogger; @Mock private GymCrmMetrics metrics; @Mock private Authentication authentication;
    private TrainerController controller;
    @BeforeEach void setUp() { controller = new TrainerController(trainerService, userLogger, metrics); lenient().when(authentication.getName()).thenReturn("actor"); }

    @Test void createsAndFindsTrainer() {
        CreateTrainerRequest create = mock(CreateTrainerRequest.class); when(trainerService.createProfile(create)).thenReturn(mock(CreatedTrainerResponse.class));
        assertEquals(201, controller.createTrainer(create).getStatusCode().value());
        when(trainerService.getProfileByUsername("known")).thenReturn(mock(TrainerResponse.class)); when(trainerService.getProfileByUsername("missing")).thenThrow(new EntityNotFoundException("trainer"));
        assertEquals(200, controller.getTrainer("known", authentication).getStatusCode().value()); assertEquals(404, controller.getTrainer("missing", authentication).getStatusCode().value());
    }

    @Test void mapsUpdateLookupAndStatusFailures() {
        UpdateTrainerRequest update = mock(UpdateTrainerRequest.class);
        when(trainerService.updateProfile("missing", update)).thenThrow(new EntityNotFoundException("trainer")); when(trainerService.updateProfile("bad", update)).thenThrow(new InvalidRequestException("invalid"));
        assertEquals(404, controller.updateTrainer("missing", update, authentication).getStatusCode().value()); assertEquals(400, controller.updateTrainer("bad", update, authentication).getStatusCode().value());
        when(trainerService.getTrainersNotAssignedToTraineeEmbedded("known")).thenReturn(java.util.List.of()); when(trainerService.getTrainersNotAssignedToTraineeEmbedded("missing")).thenThrow(new EntityNotFoundException("trainee"));
        assertEquals(200, controller.getAllTrainersNotAssignedToTrainee("known", authentication).getStatusCode().value()); assertEquals(404, controller.getAllTrainersNotAssignedToTrainee("missing", authentication).getStatusCode().value());
        doNothing().when(trainerService).setStatus("known", true); doThrow(new EntityNotFoundException("trainer")).when(trainerService).setStatus("missing", true); doThrow(new InvalidRequestException("invalid")).when(trainerService).setStatus("bad", true);
        assertEquals(200, controller.setStatus("known", true, authentication).getStatusCode().value()); assertEquals(404, controller.setStatus("missing", true, authentication).getStatusCode().value()); assertEquals(400, controller.setStatus("bad", true, authentication).getStatusCode().value());
    }
}
