package com.elioth.epam.gymcrm.event;

import org.junit.jupiter.api.Test;
import com.elioth.epam.gymcrm.messaging.TrainerWorkloadMessage;
import com.elioth.epam.gymcrm.logging.TransactionIdFilter;
import jakarta.jms.Message;
import org.slf4j.MDC;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;
import org.mockito.ArgumentCaptor;

class TrainingWorkloadEventListenerTest {

    @Test
    void publishesTrainingWorkloadEventToTheConfiguredQueueWithTransactionId() throws Exception {
        JmsTemplate jmsTemplate = mock(JmsTemplate.class);
        TrainingWorkloadEventListener listener = new TrainingWorkloadEventListener(
                jmsTemplate, "trainer.workload"
        );
        TrainingWorkloadChangedEvent event = new TrainingWorkloadChangedEvent(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2026, 8, 5), 60L, WorkloadAction.ADD
        );

        MDC.put(TransactionIdFilter.MDC_KEY, "transaction-123");
        try {
            listener.onTrainingWorkloadChanged(event);
        } finally {
            MDC.remove(TransactionIdFilter.MDC_KEY);
        }

        TrainerWorkloadMessage expected = new TrainerWorkloadMessage(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2026, 8, 5), 60L, "ADD"
        );
        ArgumentCaptor<MessagePostProcessor> postProcessor = ArgumentCaptor.forClass(MessagePostProcessor.class);
        verify(jmsTemplate).convertAndSend(eq("trainer.workload"), eq(expected), postProcessor.capture());

        Message message = mock(Message.class);
        postProcessor.getValue().postProcessMessage(message);
        verify(message).setStringProperty(TransactionIdFilter.MDC_KEY, "transaction-123");
    }
}
