package com.elioth.epam.gymcrm.event;

import org.junit.jupiter.api.Test;
import com.elioth.epam.gymcrm.messaging.TrainerWorkloadMessage;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TrainingWorkloadEventListenerTest {

    @Test
    void publishesTrainingWorkloadEventToTheConfiguredQueue() {
        JmsTemplate jmsTemplate = mock(JmsTemplate.class);
        TrainingWorkloadEventListener listener = new TrainingWorkloadEventListener(
                jmsTemplate, "trainer.workload"
        );
        TrainingWorkloadChangedEvent event = new TrainingWorkloadChangedEvent(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2026, 8, 5), 60L, WorkloadAction.ADD
        );

        listener.onTrainingWorkloadChanged(event);

        verify(jmsTemplate).convertAndSend("trainer.workload", new TrainerWorkloadMessage(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2026, 8, 5), 60L, "ADD"
        ));
    }
}
