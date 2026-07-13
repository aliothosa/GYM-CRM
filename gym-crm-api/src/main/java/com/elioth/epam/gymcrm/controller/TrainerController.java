package com.elioth.epam.gymcrm.controller;

import com.elioth.epam.gymcrm.auth.AuthSession;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.dto.request.CreateTrainerRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTrainerRequest;
import com.elioth.epam.gymcrm.dto.response.CreatedTrainerResponse;
import com.elioth.epam.gymcrm.dto.response.EmbeddedTraineeResponse;
import com.elioth.epam.gymcrm.dto.response.EmbeddedTrainerResponse;
import com.elioth.epam.gymcrm.dto.response.TrainerResponse;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.exception.InvalidRequestException;
import com.elioth.epam.gymcrm.logging.UserLogger;
import com.elioth.epam.gymcrm.service.TrainerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/trainers", produces = "application/JSON", consumes = "application/JSON")
public class TrainerController {
    private final UserLogger userLogger;
    private final Logger LOG = LoggerFactory.getLogger(TrainerController.class);

    private final TrainerService trainerService;

    @Autowired
    public TrainerController(TrainerService trainerService, UserLogger userLogger) {
        this.trainerService = trainerService;
        this.userLogger = userLogger;
    }

    @PostMapping(value = "/register")
    public ResponseEntity<CreatedTrainerResponse> createTrainer(
            @RequestBody CreateTrainerRequest createTrainerRequest
    ) {
        LOG.info("createTrainer request: {}", createTrainerRequest);

        CreatedTrainerResponse response = trainerService.createProfile(createTrainerRequest);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{username}")
    public ResponseEntity<TrainerResponse> getTrainer(
            @PathVariable String username,
            @SessionAttribute("AUTH_SESSION")
                AuthSession authSession
    ){
        userLogger.log(authSession.username(),"Attempting to get trainer by username: {}", username);

        try{
            TrainerResponse response = trainerService.getProfileByUsername(username);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(value = "/{username}")
    public ResponseEntity<TrainerResponse> updateTrainer(
            @PathVariable String username,
            @RequestBody UpdateTrainerRequest updateTrainerRequest,
            @SessionAttribute("AUTH_SESSION")
                AuthSession authSession
    ){
        userLogger.log(authSession.username(),"Attempting to update trainer by username: {}", username);

        try{
            TrainerResponse response = trainerService.updateProfile(username, updateTrainerRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (InvalidRequestException e){
            return ResponseEntity.badRequest().build();
        }

    }
    @GetMapping(value = "/not-assigned/{username}")
    public ResponseEntity<List<EmbeddedTrainerResponse>> getAllTrainersNotAssignedToTrainee(
            @PathVariable String username,
            @SessionAttribute("AUTH_SESSION")
                AuthSession authSession
    ){
        userLogger.log(authSession.username(), "Attempting to get trainers not assigned to trainee by username: {}", username);

        try{
            List<EmbeddedTrainerResponse> embeddedTrainerList = trainerService.getTrainersNotAssignedToTraineeEmbedded(username);
            return new ResponseEntity<>(embeddedTrainerList, HttpStatus.OK);
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
            trainerService.setStatus(username, status);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (InvalidRequestException e){
            return ResponseEntity.badRequest().build();
        }
    }


}
