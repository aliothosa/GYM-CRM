package com.elioth.epam.gymcrm.config;

import com.elioth.epam.gymcrm.messaging.TrainerWorkloadMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.Map;

@Configuration
public class JmsConfiguration {

    @Bean
    public MappingJackson2MessageConverter jmsMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setTypeIdMappings(Map.of(
                "trainerWorkload", TrainerWorkloadMessage.class
        ));
        return converter;
    }
}
