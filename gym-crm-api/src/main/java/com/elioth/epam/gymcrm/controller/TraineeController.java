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
import java.util.Set;


@RestController
@RequestMapping(value = "/trainees", produces = {"application/json"})
@Api(tags = "Trainees", description = "Trainee profile management operations")
public class TraineeController {

    private final UserLogger userLogger;
    private final Logger LOG = LoggerFactory.getLogger(TraineeController.class);

    private final TraineeService traineeService;

    @Autowired
    public TraineeController(TraineeService traineeService, UserLogger userLogger) {
        this.userLogger = userLogger;
        this.traineeService = traineeService;

    }

    @ApiOperation(
            value = "Register trainee",
            notes = "Creates a new trainee profile",
            response = CreatedTraineeResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 201, message = "Trainee registered successfully"),
            @ApiResponse(code = 400, message = "Invalid trainee information"),
            @ApiResponse(code = 403, message = "Authentication required")
    })
    @PostMapping(value = "/register")
    public ResponseEntity<CreatedTraineeResponse> addTrainee(
            @ApiParam(value = "Information required to register a trainee", required = true)
            @RequestBody CreateTraineeRequest createTraineeRequest
    ) {
        LOG.info("addTrainee request: {}",  createTraineeRequest);

        try {
            CreatedTraineeResponse response = traineeService.createProfile(createTraineeRequest);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (InvalidRequestException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @ApiOperation(
            value = "Get trainee profile",
            notes = "Returns the profile information of the trainee identified by username",
            response = TraineeResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee profile retrieved successfully"),
            @ApiResponse(code = 403, message = "Authentication required"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    @PostMapping(value = "/{username}")
    public ResponseEntity<TraineeResponse> getTraineeProfile(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable String username,
            @ApiParam(hidden = true)
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


    @ApiOperation(
            value = "Update trainee profile",
            notes = "Updates the profile information of the trainee identified by username",
            response = TraineeResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee profile updated successfully"),
            @ApiResponse(code = 400, message = "Invalid trainee information"),
            @ApiResponse(code = 403, message = "Authentication required"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    @PutMapping(value = "/{username}")
    public ResponseEntity<TraineeResponse> updateTraineeProfile(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable String username,
            @ApiParam(value = "Updated trainee profile information", required = true)
            @RequestBody UpdateTraineeRequest updateTraineeRequest,
            @ApiParam(hidden = true)
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

    @ApiOperation(
            value = "Delete trainee profile",
            notes = "Deletes the trainee profile identified by username",
            response = Void.class
    )
    @ApiResponses({
            @ApiResponse(code = 204, message = "Trainee profile deleted successfully"),
            @ApiResponse(code = 403, message = "Authentication required"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    @DeleteMapping(value = "/{username}")
    public ResponseEntity<Void> deleteTraineeProfile(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable String username,
            @ApiParam(hidden = true)
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

    @ApiOperation(
            value = "Update trainee trainers",
            notes = "Replaces the trainers assigned to the trainee identified by username",
            response = EmbeddedTrainerResponse.class,
            responseContainer = "Set"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee trainers updated successfully"),
            @ApiResponse(code = 403, message = "Authentication required"),
            @ApiResponse(code = 404, message = "Trainee or trainer not found")
    })
    @PutMapping(value = "/update-trainer-list/{username}")
    public ResponseEntity<Set<EmbeddedTrainerResponse>> updateTraineeList(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable String username,
            @ApiParam(value = "Usernames of trainers to assign", required = true)
            @RequestParam Set<String> trainerUsernames,
            @ApiParam(hidden = true)
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

    @ApiOperation(
            value = "Set trainee status",
            notes = "Activates or deactivates the trainee identified by username",
            response = Void.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee status updated successfully"),
            @ApiResponse(code = 400, message = "Invalid status change"),
            @ApiResponse(code = 403, message = "Authentication required"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    @PatchMapping(value = "/{username}/satus")
    public ResponseEntity<Void> setStatus(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable String username,
            @ApiParam(value = "New active status", required = true)
            @RequestParam Boolean status,
            @ApiParam(hidden = true)
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
