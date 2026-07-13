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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/trainers", produces = "application/JSON", consumes = "application/JSON")
@Api(tags = "Trainers", description = "Trainer profile management operations")
public class TrainerController {
    private final UserLogger userLogger;
    private final Logger LOG = LoggerFactory.getLogger(TrainerController.class);

    private final TrainerService trainerService;

    @Autowired
    public TrainerController(TrainerService trainerService, UserLogger userLogger) {
        this.trainerService = trainerService;
        this.userLogger = userLogger;
    }

    @ApiOperation(
            value = "Register trainer",
            notes = "Creates a new trainer profile",
            response = CreatedTrainerResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 201, message = "Trainer registered successfully"),
            @ApiResponse(code = 403, message = "Authentication required")
    })
    @PostMapping(value = "/register")
    public ResponseEntity<CreatedTrainerResponse> createTrainer(
            @ApiParam(value = "Information required to register a trainer", required = true)
            @RequestBody CreateTrainerRequest createTrainerRequest
    ) {
        LOG.info("createTrainer request: {}", createTrainerRequest);

        CreatedTrainerResponse response = trainerService.createProfile(createTrainerRequest);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(
            value = "Get trainer profile",
            notes = "Returns the profile information of the trainer identified by username",
            response = TrainerResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainer profile retrieved successfully"),
            @ApiResponse(code = 403, message = "Authentication required"),
            @ApiResponse(code = 404, message = "Trainer not found")
    })
    @GetMapping(value = "/{username}")
    public ResponseEntity<TrainerResponse> getTrainer(
            @ApiParam(value = "Trainer username", required = true)
            @PathVariable String username,
            @ApiParam(hidden = true)
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

    @ApiOperation(
            value = "Update trainer profile",
            notes = "Updates the profile information of the trainer identified by username",
            response = TrainerResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainer profile updated successfully"),
            @ApiResponse(code = 400, message = "Invalid trainer information"),
            @ApiResponse(code = 403, message = "Authentication required"),
            @ApiResponse(code = 404, message = "Trainer or training type not found")
    })
    @PutMapping(value = "/{username}")
    public ResponseEntity<TrainerResponse> updateTrainer(
            @ApiParam(value = "Trainer username", required = true)
            @PathVariable String username,
            @ApiParam(value = "Updated trainer profile information", required = true)
            @RequestBody UpdateTrainerRequest updateTrainerRequest,
            @ApiParam(hidden = true)
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
    @ApiOperation(
            value = "Get unassigned trainers",
            notes = "Returns trainers that are not assigned to the specified trainee",
            response = EmbeddedTrainerResponse.class,
            responseContainer = "List"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Unassigned trainers retrieved successfully"),
            @ApiResponse(code = 403, message = "Authentication required"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    @GetMapping(value = "/not-assigned/{username}")
    public ResponseEntity<List<EmbeddedTrainerResponse>> getAllTrainersNotAssignedToTrainee(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable String username,
            @ApiParam(hidden = true)
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
    @ApiOperation(
            value = "Set trainer status",
            notes = "Activates or deactivates the trainer identified by username",
            response = Void.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainer status updated successfully"),
            @ApiResponse(code = 400, message = "Invalid status change"),
            @ApiResponse(code = 403, message = "Authentication required"),
            @ApiResponse(code = 404, message = "Trainer not found")
    })
    @PatchMapping(value = "/{username}/satus")
    public ResponseEntity<Void> setStatus(
            @ApiParam(value = "Trainer username", required = true)
            @PathVariable String username,
            @ApiParam(value = "New active status", required = true)
            @RequestParam Boolean status,
            @ApiParam(hidden = true)
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
