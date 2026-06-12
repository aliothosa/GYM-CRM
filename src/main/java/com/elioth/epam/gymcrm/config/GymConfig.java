package com.elioth.epam.gymcrm.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("com.elioth.epam.gycrm")
@PropertySource(value = "classpath:application.yaml")
public class GymConfig {

}
