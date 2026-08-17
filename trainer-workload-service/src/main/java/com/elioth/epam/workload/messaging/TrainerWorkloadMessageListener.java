package com.elioth.epam.workload.messaging;

import com.elioth.epam.workload.domain.ActionType;
import com.elioth.epam.workload.dto.request.TrainerWorkloadRequest;
import com.elioth.epam.workload.exception.InvalidWorkloadException;
import com.elioth.epam.workload.service.TrainerWorkloadService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TrainerWorkloadMessageListener {

    private final TrainerWorkloadService workloadService;
    private final Validator validator;

    public TrainerWorkloadMessageListener(
            TrainerWorkloadService workloadService,
            Validator validator
    ) {
        this.workloadService = workloadService;
        this.validator = validator;
    }

    @JmsListener(destination = "${gymcrm.messaging.trainer-workload.queue}")
    public void onTrainingWorkloadChanged(TrainerWorkloadMessage event) {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                event.trainerUsername(),
                event.trainerFirstName(),
                event.trainerLastName(),
                event.trainerActive(),
                event.trainingDate(),
                event.trainingDurationMinutes(),
                actionType(event.action())
        );
        validate(request);
        workloadService.applyWorkload(request);
    }

    private ActionType actionType(String action) {
        try {
            return ActionType.valueOf(action);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new InvalidWorkloadException("Invalid trainer workload message action");
        }
    }

    private void validate(TrainerWorkloadRequest request) {
        Set<ConstraintViolation<TrainerWorkloadRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new InvalidWorkloadException("Invalid trainer workload message");
        }
    }
}
