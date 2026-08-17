package com.elioth.epam.gymcrm.event;

import org.springframework.beans.factory.annotation.Value;
import com.elioth.epam.gymcrm.messaging.TrainerWorkloadMessage;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TrainingWorkloadEventListener {

    private final JmsTemplate jmsTemplate;
    private final String queueName;

    public TrainingWorkloadEventListener(
            JmsTemplate jmsTemplate,
            @Value("${gymcrm.messaging.trainer-workload.queue}") String queueName
    ) {
        this.jmsTemplate = jmsTemplate;
        this.queueName = queueName;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onTrainingWorkloadChanged(
            TrainingWorkloadChangedEvent event
    ) {
        jmsTemplate.convertAndSend(queueName, new TrainerWorkloadMessage(
                event.trainerUsername(),
                event.trainerFirstName(),
                event.trainerLastName(),
                event.trainerActive(),
                event.trainingDate(),
                event.trainingDurationMinutes(),
                event.action().name()
        ));
    }
}
