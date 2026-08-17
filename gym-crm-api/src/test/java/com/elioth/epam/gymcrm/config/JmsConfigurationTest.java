package com.elioth.epam.gymcrm.config;

import com.elioth.epam.gymcrm.messaging.TrainerWorkloadMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JmsConfigurationTest {

    @Test
    void serializesAndDeserializesTrainingWorkloadMessages() throws Exception {
        Session session = mock(Session.class);
        TextMessage message = mock(TextMessage.class);
        when(session.createTextMessage(anyString())).thenAnswer(invocation -> {
            when(message.getText()).thenReturn(invocation.getArgument(0));
            return message;
        });
        when(message.getStringProperty("_type")).thenReturn("trainerWorkload");
        TrainerWorkloadMessage event = new TrainerWorkloadMessage(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2026, 8, 5), 60L, "ADD"
        );

        var converter = new JmsConfiguration().jmsMessageConverter(
                new ObjectMapper().findAndRegisterModules()
        );
        Object deserialized = converter.fromMessage(converter.toMessage(event, session));

        assertEquals(event, deserialized);
    }
}
