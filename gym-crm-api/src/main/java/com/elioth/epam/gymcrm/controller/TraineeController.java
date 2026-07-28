package com.elioth.epam.gymcrm.controller;

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
import com.elioth.epam.gymcrm.metrics.GymCrmMetrics;
import com.elioth.epam.gymcrm.service.TraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping(value = "/trainees", produces = {"application/json"})
@Tag(name = "Trainees", description = "Trainee profile management operations")
public class TraineeController {

    private final UserLogger userLogger;
    private final Logger LOG = LoggerFactory.getLogger(TraineeController.class);

    private final TraineeService traineeService;
    private final GymCrmMetrics metrics;

    @Autowired
    public TraineeController(TraineeService traineeService, UserLogger userLogger, GymCrmMetrics metrics) {
        this.userLogger = userLogger;
        this.traineeService = traineeService;
        this.metrics = metrics;
    }

    @Operation(
            summary = "Register trainee",
            description = "Creates a new trainee profile"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Trainee registered successfully",
                    content = @Content(schema = @Schema(implementation = CreatedTraineeResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid trainee information")
    })
    @PostMapping(value = "/register")
    @SecurityRequirements
    public ResponseEntity<CreatedTraineeResponse> addTrainee(
            @Parameter(description = "Information required to register a trainee", required = true)
            @RequestBody CreateTraineeRequest createTraineeRequest
    ) {
        LOG.info("addTrainee request: {}",  createTraineeRequest);

        try {
            CreatedTraineeResponse response = traineeService.createProfile(createTraineeRequest);
            metrics.incrementTraineesCreated();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (InvalidRequestException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Get trainee profile",
            description = "Returns the profile information of the trainee identified by username"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Trainee profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TraineeResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PostMapping(value = "/{username}")
    public ResponseEntity<TraineeResponse> getTraineeProfile(
            @Parameter(description = "Trainee username", required = true)
            @PathVariable String username
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


    @Operation(
            summary = "Update trainee profile",
            description = "Updates the profile information of the trainee identified by username"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Trainee profile updated successfully",
                    content = @Content(schema = @Schema(implementation = TraineeResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid trainee information"),
            @ApiResponse(responseCode = "403", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PutMapping(value = "/{username}")
    public ResponseEntity<TraineeResponse> updateTraineeProfile(
            @Parameter(description = "Trainee username", required = true)
            @PathVariable String username,
            @Parameter(description = "Updated trainee profile information", required = true)
            @RequestBody UpdateTraineeRequest updateTraineeRequest,
            @Parameter(hidden = true)
            Authentication authentication
    ){
        userLogger.log(authentication.getName(), "Attempting to update trainee profile with username: {}", authentication.getName());

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

    @Operation(
            summary = "Delete trainee profile",
            description = "Deletes the trainee profile identified by username"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Trainee profile deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @DeleteMapping(value = "/{username}")
    public ResponseEntity<Void> deleteTraineeProfile(
            @Parameter(description = "Trainee username", required = true)
            @PathVariable String username,
            @Parameter(hidden = true)
            Authentication authentication
    ){
        userLogger.log(authentication.getName(), "Attempting to delete trainee profile with username: {}", username);

        try{
            traineeService.deleteProfile(username);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Update trainee trainers",
            description = "Replaces the trainers assigned to the trainee identified by username"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Trainee trainers updated successfully",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = EmbeddedTrainerResponse.class),
                            uniqueItems = true
                    ))
            ),
            @ApiResponse(responseCode = "403", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Trainee or trainer not found")
    })
    @PutMapping(value = "/update-trainer-list/{username}")
    public ResponseEntity<Set<EmbeddedTrainerResponse>> updateTraineeList(
            @Parameter(description = "Trainee username", required = true)
            @PathVariable String username,
            @Parameter(description = "Usernames of trainers to assign", required = true)
            @RequestParam Set<String> trainerUsernames,
            @Parameter(hidden = true)
            Authentication authentication
    ){
        userLogger.log(authentication.getName(), "Attempting to update trainee list with username: {}", username);
        try{
            Set<EmbeddedTrainerResponse> response = traineeService.updateTrainersToTrainee(username, trainerUsernames);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Set trainee status",
            description = "Activates or deactivates the trainee identified by username"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status change"),
            @ApiResponse(responseCode = "403", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PatchMapping(value = "/{username}/satus")
    public ResponseEntity<Void> setStatus(
            @Parameter(description = "Trainee username", required = true)
            @PathVariable String username,
            @Parameter(description = "New active status", required = true)
            @RequestParam Boolean status,
            @Parameter(hidden = true)
            Authentication authentication
    ){
        userLogger.log(authentication.getName(), "Attempting to set trainee with username {} status to: {}", username, status);

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
