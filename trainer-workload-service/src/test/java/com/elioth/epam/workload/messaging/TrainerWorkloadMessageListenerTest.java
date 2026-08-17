package com.elioth.epam.workload.messaging;

import com.elioth.epam.workload.dto.request.TrainerWorkloadRequest;
import com.elioth.epam.workload.exception.InvalidWorkloadException;
import com.elioth.epam.workload.service.TrainerWorkloadService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TrainerWorkloadMessageListenerTest {

    @Test
    void appliesTheExistingWorkloadLogicForTheReceivedMessage() {
        TrainerWorkloadService service = mock(TrainerWorkloadService.class);
        TrainerWorkloadMessageListener listener = listener(service);

        listener.onTrainingWorkloadChanged(new TrainerWorkloadMessage(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2026, 8, 5), 60L, "ADD"
        ));

        verify(service).applyWorkload(argThat(request -> matchesExpectedRequest(request)));
    }

    @Test
    void rejectsAnInvalidActionWithoutCallingBusinessLogic() {
        TrainerWorkloadService service = mock(TrainerWorkloadService.class);

        assertThrows(InvalidWorkloadException.class, () -> listener(service)
                .onTrainingWorkloadChanged(message("UNKNOWN")));

        verifyNoInteractions(service);
    }

    @Test
    void propagatesBusinessFailuresToTheJmsContainerErrorHandler() {
        TrainerWorkloadService service = mock(TrainerWorkloadService.class);
        doThrow(new InvalidWorkloadException("Workload cannot become negative"))
                .when(service).applyWorkload(any());

        assertThrows(InvalidWorkloadException.class, () -> listener(service)
                .onTrainingWorkloadChanged(message("ADD")));
    }

    @Test
    void rejectsAnIncompleteMessageWithoutCallingBusinessLogic() {
        TrainerWorkloadService service = mock(TrainerWorkloadService.class);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        TrainerWorkloadMessageListener listener = new TrainerWorkloadMessageListener(service, validator);

        assertThrows(InvalidWorkloadException.class, () -> listener
                .onTrainingWorkloadChanged(new TrainerWorkloadMessage(
                        "john.doe", "John", "Doe", null,
                        LocalDate.of(2026, 8, 5), 60L, "ADD"
                )));

        verifyNoInteractions(service);
    }

    private TrainerWorkloadMessageListener listener(TrainerWorkloadService service) {
        Validator validator = mock(Validator.class);
        when(validator.validate(any(TrainerWorkloadRequest.class))).thenReturn(Set.of());
        return new TrainerWorkloadMessageListener(service, validator);
    }

    private TrainerWorkloadMessage message(String action) {
        return new TrainerWorkloadMessage(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2026, 8, 5), 60L, action
        );
    }

    private boolean matchesExpectedRequest(TrainerWorkloadRequest request) {
        return request.trainerUsername().equals("john.doe")
                && request.trainingDate().equals(LocalDate.of(2026, 8, 5))
                && request.trainingDurationMinutes().equals(60L)
                && request.actionType().name().equals("ADD");
    }
}
