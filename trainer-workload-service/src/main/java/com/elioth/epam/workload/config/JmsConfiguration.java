package com.elioth.epam.workload.config;

import com.elioth.epam.workload.messaging.TrainerWorkloadMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jms.autoconfigure.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import jakarta.jms.ConnectionFactory;
import java.util.Map;

@Configuration
public class JmsConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(JmsConfiguration.class);

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

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer
    ) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setErrorHandler(throwable -> LOG.error(
                "operation=JMS_WORKLOAD_MESSAGE result=FAILED exceptionType={}",
                throwable.getClass().getSimpleName(),
                throwable
        ));
        return factory;
    }
}
