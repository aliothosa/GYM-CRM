package com.elioth.epam.gymcrm.controller;

import com.elioth.epam.gymcrm.auth.AuthSession;
import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.dto.request.CreateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTraineeRequest;
import com.elioth.epam.gymcrm.dto.response.CreatedTraineeResponse;
import com.elioth.epam.gymcrm.dto.response.EmbeddedTrainerResponse;
import com.elioth.epam.gymcrm.dto.response.TraineeResponse;
import com.elioth.epam.gymcrm.dto.response.TrainerResponse;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.exception.InvalidRequestException;
import com.elioth.epam.gymcrm.logging.UserLogger;
import com.elioth.epam.gymcrm.service.TraineeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping(value = "/trainees", produces = {"application/json"})
public class TraineeController {

    private final UserLogger userLogger;
    private final Logger LOG = LoggerFactory.getLogger(TraineeController.class);

    private final TraineeService traineeService;

    @Autowired
    public TraineeController(TraineeService traineeService, UserLogger userLogger) {
        this.userLogger = userLogger;
        this.traineeService = traineeService;

    }

    @PostMapping(value = "/register")
    public ResponseEntity<CreatedTraineeResponse> addTrainee(@RequestBody CreateTraineeRequest createTraineeRequest) {
        LOG.info("addTrainee request: {}",  createTraineeRequest);

        try {
            CreatedTraineeResponse response = traineeService.createProfile(createTraineeRequest);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (InvalidRequestException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/{username}")
    public ResponseEntity<TraineeResponse> getTraineeProfile(
            @PathVariable String username,
            @SessionAttribute("AUTH_SESSION")
                AuthSession session
    ){
        userLogger.log(username, "Requested information for trainee with username: {}", username);

        try {
            TraineeResponse response = traineeService.getProfileByUsername(username);
            return new ResponseEntity<>(
                    response,
                    HttpStatus.OK
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping(value = "/{username}")
    public ResponseEntity<TraineeResponse> updateTraineeProfile(
            @PathVariable String username,
            @RequestBody UpdateTraineeRequest updateTraineeRequest,
            @SessionAttribute("AUTH_SESSION")
                AuthSession authSession
    ){
        userLogger.log(authSession.username(), "Attempting to update trainee profile with username: {}", authSession.username());

        try{
            TraineeResponse response = traineeService.updateProfile(username, updateTraineeRequest);
            return new ResponseEntity<>(
                    response,
                    HttpStatus.OK
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidRequestException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping(value = "/{username}")
    public ResponseEntity<Void> deleteTraineeProfile(
            @PathVariable String username,
            @SessionAttribute("AUTH_SESSION")
                AuthSession authSession
    ){
        userLogger.log(authSession.username(), "Attempting to delete trainee profile with username: {}", username);

        try{
            traineeService.deleteProfile(username);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(value = "/update-trainer-list/{username}")
    public ResponseEntity<Set<EmbeddedTrainerResponse>> updateTraineeList(
            @PathVariable String username,
            @RequestParam Set<String> trainerUsernames,
            @SessionAttribute("AUTH_SESSION")
            AuthSession authSession
    ){
        userLogger.log(authSession.username(), "Attempting to update trainee list with username: {}", username);
        try{
            Set<EmbeddedTrainerResponse> response = traineeService.updateTrainersToTrainee(username, trainerUsernames);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping(value = "/{username}/satus")
    public ResponseEntity<Void> setStatus(
            @PathVariable String username,
            @RequestParam Boolean status,
            @SessionAttribute("AUTH_SESSION")
                AuthSession authSession
    ){
        userLogger.log(authSession.username(), "Attempting to set trainee with username {} status to: {}", username, status);

        try{
            traineeService.setStatus(username, status);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (InvalidRequestException e){
            return ResponseEntity.badRequest().build();
        }
    }


}
