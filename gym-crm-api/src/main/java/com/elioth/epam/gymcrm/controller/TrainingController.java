package com.elioth.epam.gymcrm.controller;

import com.elioth.epam.gymcrm.auth.AuthSession;
import com.elioth.epam.gymcrm.dto.request.CreateTrainingRestRequest;
import com.elioth.epam.gymcrm.dto.request.GetTraineeTrainingsRestRequest;
import com.elioth.epam.gymcrm.dto.request.GetTrainerTrainingsRestRequest;
import com.elioth.epam.gymcrm.dto.response.TraineeTrainingResponse;
import com.elioth.epam.gymcrm.dto.response.TrainerTrainingResponse;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.exception.InvalidRequestException;
import com.elioth.epam.gymcrm.logging.UserLogger;
import com.elioth.epam.gymcrm.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TrainingController {

    private UserLogger userLogger;

    private TrainingService trainingService;

    @Autowired
    public TrainingController(UserLogger userLogger, TrainingService trainingService) {
        this.userLogger = userLogger;
        this.trainingService = trainingService;
    }

    @PostMapping(value = "/trainings")
    public ResponseEntity<Void> addTraining(
            @RequestBody CreateTrainingRestRequest createTrainingRequest,
            @SessionAttribute("AUTH_SESSION")
                AuthSession authSession
    ) {
       userLogger.log(authSession.username(), "Creating a new training for Trainee: {} with Trainer: {}", createTrainingRequest.traineeUsername(), createTrainingRequest.trainerUsername());

       try{
           trainingService.createTraining(createTrainingRequest);
           return ResponseEntity.ok().build();
       } catch (EntityNotFoundException e){
           return ResponseEntity.badRequest().build();
       }   catch (InvalidRequestException e){
            return ResponseEntity.notFound().build();
       }
    }


    @GetMapping(value = "/trainings/training-types")
    public ResponseEntity<List<String>> getAllTrainingTypes(
            @SessionAttribute("AUTH_SESSION")
            AuthSession authSession
    ) {
        userLogger.log(authSession.username(), "getting all training types");

        List<String> names = trainingService.getTrainingTypeNames();

        return new ResponseEntity<>(names, HttpStatus.OK);
    }


    @GetMapping(value = "/trainees/{username}/trainings")
    public ResponseEntity<List<TraineeTrainingResponse>> getTrainingsOfTrainee(
            @PathVariable String username,
            @RequestBody GetTraineeTrainingsRestRequest request,
            @SessionAttribute("AUTH_SESSION")
                AuthSession authSession
    ){
        userLogger.log(authSession.username(), "getting all training for user: {}", username);

        try{
            List<TraineeTrainingResponse> trainings = trainingService.getTrainingsByTraineeUsernameAndCriteria(username, request);
            return new ResponseEntity<>(trainings, HttpStatus.OK);
        } catch (InvalidRequestException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/trainers/{username}/trainings")
    public ResponseEntity<List<TrainerTrainingResponse>> getTrainingsOfTrainer(
            @PathVariable("username") String username,
            @RequestBody GetTrainerTrainingsRestRequest request,
            @SessionAttribute("AUTH_SESSION")
                AuthSession authSession
    ){
        userLogger.log(authSession.username(), "getting training of trainer: {}", username);

        try{
            List<TrainerTrainingResponse> trainings = trainingService.getTrainingsByTrainerUsernameAndCriteria(username, request);
            return new ResponseEntity<>(trainings, HttpStatus.OK);
        } catch( InvalidRequestException e ){
            return ResponseEntity.badRequest().build();
        }

    }





}
