package com.elioth.epam.gymcrm.config;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class EurekaClientConfigurationTest {

    @Test
    void usesStableApplicationNameAndContainerAwareEurekaUrl() throws IOException {
        Properties properties = new Properties();
        try (var input = getClass().getResourceAsStream("/application.properties")) {
            properties.load(input);
        }

        assertEquals("gym-crm-api", properties.getProperty("spring.application.name"));
        assertEquals("${EUREKA_URL:http://localhost:8761/eureka/}",
                properties.getProperty("eureka.client.serviceUrl.defaultZone"));
    }

    @Test
    void configuresAWorkloadBuilderWithAnExplicitEurekaLoadBalancerInterceptor() {
        LoadBalancerClient loadBalancerClient = mock(LoadBalancerClient.class);

        RestClient.Builder builder = new WorkloadClientConfig().workloadRestClientBuilder(
                Duration.ofSeconds(2), Duration.ofSeconds(3), loadBalancerClient);

        assertNotNull(builder.build());
    }
}
