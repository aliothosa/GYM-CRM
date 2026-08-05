package com.elioth.epam.workload;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EurekaClientConfigurationTest {

    @Test
    void usesStableApplicationNameAndContainerAwareEurekaUrl() throws IOException {
        Properties properties = properties();

        assertEquals("trainer-workload-service", properties.getProperty("spring.application.name"));
        assertEquals("${EUREKA_URL:http://localhost:8761/eureka/}",
                properties.getProperty("eureka.client.serviceUrl.defaultZone"));
    }

    private Properties properties() throws IOException {
        Properties properties = new Properties();
        try (var input = getClass().getResourceAsStream("/application.properties")) {
            properties.load(input);
        }
        return properties;
    }
}
