package com.elioth.epam.gymcrm.controller;

import com.elioth.epam.gymcrm.service.TraineeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/trainees", produces = {"application/json"})
public class TraineeController {
    private final TraineeService traineeService;

    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @GetMapping("/{id}")
    public Object getTrainee(@PathVariable Long id) {
        return traineeService.getProfileById(id);
    }
}
