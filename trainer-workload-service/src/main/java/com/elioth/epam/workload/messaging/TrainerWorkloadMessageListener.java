package com.elioth.epam.workload.messaging;

import com.elioth.epam.workload.domain.ActionType;
import com.elioth.epam.workload.dto.request.TrainerWorkloadRequest;
import com.elioth.epam.workload.exception.InvalidWorkloadException;
import com.elioth.epam.workload.logging.TransactionIdFilter;
import com.elioth.epam.workload.service.TrainerWorkloadService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class TrainerWorkloadMessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(TrainerWorkloadMessageListener.class);
    private static final String TRANSACTION_ID = TransactionIdFilter.MDC_KEY;

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
    public void onTrainingWorkloadChanged(
            TrainerWorkloadMessage event,
            @Header(name = TRANSACTION_ID, required = false) String transactionId
    ) {
        String effectiveTransactionId = TransactionIdFilter.isSafeTransactionId(transactionId)
                ? transactionId
                : UUID.randomUUID().toString();
        MDC.put(TRANSACTION_ID, effectiveTransactionId);
        LOG.info("transaction=START operation=JMS_WORKLOAD_MESSAGE");
        try {
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
            LOG.info("transaction=END operation=JMS_WORKLOAD_MESSAGE result=SUCCESS");
        } catch (RuntimeException exception) {
            LOG.error(
                    "transaction=END operation=JMS_WORKLOAD_MESSAGE result=FAILED exceptionType={}",
                    exception.getClass().getSimpleName()
            );
            throw exception;
        } finally {
            MDC.remove(TRANSACTION_ID);
        }
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
