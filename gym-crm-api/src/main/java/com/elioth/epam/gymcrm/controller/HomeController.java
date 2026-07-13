package com.elioth.epam.gymcrm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> apiIndex() {
        return Map.of(
                "application", "GYM-CRM API",
                "documentation", "/swagger-ui/index.html",
                "openApiSpecification", "/v3/api-docs",
                "login", "/auth/login",
                "registerTrainee", "/trainees/register",
                "registerTrainer", "/trainers/register",
                "trainingTypes", "/trainings/training-types"
        );
    }
}
