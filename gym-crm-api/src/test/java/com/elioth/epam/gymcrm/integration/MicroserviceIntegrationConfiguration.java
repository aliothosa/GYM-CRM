package com.elioth.epam.gymcrm.integration;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@ContextConfiguration(classes = MicroserviceIntegrationConfiguration.class)
public class MicroserviceIntegrationConfiguration {
}
