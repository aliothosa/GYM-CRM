package com.elioth.epam.gymcrm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GymCrmApiApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(GymCrmApiApplication.class);
        application.setWebApplicationType(WebApplicationType.SERVLET);
        application.run(args);
    }

}
