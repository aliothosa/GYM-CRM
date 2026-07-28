package com.elioth.epam.gymcrm.controller;

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
import com.elioth.epam.gymcrm.metrics.GymCrmMetrics;
import com.elioth.epam.gymcrm.service.TrainerService;
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


@RestController
@RequestMapping(value = "/trainers", produces = "application/JSON", consumes = "application/JSON")
@Tag(name = "Trainers", description = "Trainer profile management operations")
public class TrainerController {
    private final UserLogger userLogger;
    private final Logger LOG = LoggerFactory.getLogger(TrainerController.class);

    private final TrainerService trainerService;
    private final GymCrmMetrics metrics;

    @Autowired
    public TrainerController(TrainerService trainerService, UserLogger userLogger, GymCrmMetrics metrics) {
        this.trainerService = trainerService;
        this.userLogger = userLogger;
        this.metrics = metrics;
    }

    @Operation(
            summary = "Register trainer",
            description = "Creates a new trainer profile"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Trainer registered successfully",
                    content = @Content(schema = @Schema(implementation = CreatedTrainerResponse.class))
            )
    })
    @PostMapping(value = "/register")
    @SecurityRequirements
    public ResponseEntity<CreatedTrainerResponse> createTrainer(
            @Parameter(description = "Information required to register a trainer", required = true)
            @RequestBody CreateTrainerRequest createTrainerRequest
    ) {
        LOG.info("createTrainer request: {}", createTrainerRequest);

        CreatedTrainerResponse response = trainerService.createProfile(createTrainerRequest);
        metrics.incrementTrainersCreated();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get trainer profile",
            description = "Returns the profile information of the trainer identified by username"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Trainer profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TrainerResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @GetMapping(value = "/{username}")
    public ResponseEntity<TrainerResponse> getTrainer(
            @Parameter(description = "Trainer username", required = true)
            @PathVariable String username,
            @Parameter(hidden = true)
            Authentication authentication
    ){
        userLogger.log(authentication.getName(),"Attempting to get trainer by username: {}", username);

        try{
            TrainerResponse response = trainerService.getProfileByUsername(username);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Update trainer profile",
            description = "Updates the profile information of the trainer identified by username"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Trainer profile updated successfully",
                    content = @Content(schema = @Schema(implementation = TrainerResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid trainer information"),
            @ApiResponse(responseCode = "403", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Trainer or training type not found")
    })
    @PutMapping(value = "/{username}")
    public ResponseEntity<TrainerResponse> updateTrainer(
            @Parameter(description = "Trainer username", required = true)
            @PathVariable String username,
            @Parameter(description = "Updated trainer profile information", required = true)
            @RequestBody UpdateTrainerRequest updateTrainerRequest,
            @Parameter(hidden = true)
            Authentication authentication
    ){
        userLogger.log(authentication.getName(),"Attempting to update trainer by username: {}", username);

        try{
            TrainerResponse response = trainerService.updateProfile(username, updateTrainerRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (InvalidRequestException e){
            return ResponseEntity.badRequest().build();
        }

    }
    @Operation(
            summary = "Get unassigned trainers",
            description = "Returns trainers that are not assigned to the specified trainee"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Unassigned trainers retrieved successfully",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = EmbeddedTrainerResponse.class)
                    ))
            ),
            @ApiResponse(responseCode = "403", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping(value = "/not-assigned/{username}")
    public ResponseEntity<List<EmbeddedTrainerResponse>> getAllTrainersNotAssignedToTrainee(
            @Parameter(description = "Trainee username", required = true)
            @PathVariable String username,
            @Parameter(hidden = true)
            Authentication authentication
    ){
        userLogger.log(authentication.getName(), "Attempting to get trainers not assigned to trainee by username: {}", username);

        try{
            List<EmbeddedTrainerResponse> embeddedTrainerList = trainerService.getTrainersNotAssignedToTraineeEmbedded(username);
            return new ResponseEntity<>(embeddedTrainerList, HttpStatus.OK);
        } catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }
    @Operation(
            summary = "Set trainer status",
            description = "Activates or deactivates the trainer identified by username"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status change"),
            @ApiResponse(responseCode = "403", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @PatchMapping(value = "/{username}/satus")
    public ResponseEntity<Void> setStatus(
            @Parameter(description = "Trainer username", required = true)
            @PathVariable String username,
            @Parameter(description = "New active status", required = true)
            @RequestParam Boolean status,
            @Parameter(hidden = true)
            Authentication authentication
    ){
        userLogger.log(authentication.getName(), "Attempting to set trainee with username {} status to: {}", username, status);

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
