package com.elioth.epam.gymcrm.controller;

import com.elioth.epam.gymcrm.dto.request.CreateTrainingRestRequest;
import com.elioth.epam.gymcrm.dto.request.GetTraineeTrainingsRestRequest;
import com.elioth.epam.gymcrm.dto.request.GetTrainerTrainingsRestRequest;
import com.elioth.epam.gymcrm.dto.response.TraineeTrainingResponse;
import com.elioth.epam.gymcrm.dto.response.TrainerTrainingResponse;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.exception.InvalidRequestException;
import com.elioth.epam.gymcrm.logging.UserLogger;
import com.elioth.epam.gymcrm.metrics.GymCrmMetrics;
import com.elioth.epam.gymcrm.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Trainings", description = "Training management operations")
public class TrainingController {

    private UserLogger userLogger;

    private TrainingService trainingService;
    private GymCrmMetrics metrics;

    @Autowired
    public TrainingController(UserLogger userLogger, TrainingService trainingService, GymCrmMetrics metrics) {
        this.userLogger = userLogger;
        this.trainingService = trainingService;
        this.metrics = metrics;
    }

    @Operation(
            summary = "Add training",
            description = "Creates a training between an existing trainee and trainer"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training created successfully"),
            @ApiResponse(responseCode = "400", description = "Trainee, trainer, or training type not found"),
            @ApiResponse(responseCode = "403", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Invalid training information")
    })
    @PostMapping(value = "/trainings")
    public ResponseEntity<Void> addTraining(
            @Parameter(description = "Information required to create a training", required = true)
            @RequestBody CreateTrainingRestRequest createTrainingRequest,
            @Parameter(hidden = true)
            Authentication authentication
    ) {
       userLogger.log(authentication.getName(), "Creating a new training for Trainee: {} with Trainer: {}", createTrainingRequest.traineeUsername(), createTrainingRequest.trainerUsername());

       try{
           trainingService.createTraining(createTrainingRequest);
           metrics.incrementTrainingsCreated();
           return ResponseEntity.ok().build();
       } catch (EntityNotFoundException e){
           return ResponseEntity.badRequest().build();
       }   catch (InvalidRequestException e){
            return ResponseEntity.notFound().build();
       }
    }


    @Operation(
            summary = "Get training types",
            description = "Returns all available training type names"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Training types retrieved successfully",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = String.class)
                    ))
            ),
            @ApiResponse(responseCode = "403", description = "Authentication required")
    })
    @GetMapping(value = "/trainings/training-types")
    public ResponseEntity<List<String>> getAllTrainingTypes(
            @Parameter(hidden = true)
            Authentication authentication
    ) {
        userLogger.log(authentication.getName(), "getting all training types");

        List<String> names = trainingService.getTrainingTypeNames();

        return new ResponseEntity<>(names, HttpStatus.OK);
    }


    @Operation(
            summary = "Get trainee trainings",
            description = "Returns trainings for a trainee using the supplied search criteria"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Trainee trainings retrieved successfully",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = TraineeTrainingResponse.class)
                    ))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid training search criteria"),
            @ApiResponse(responseCode = "403", description = "Authentication required")
    })
    @GetMapping(value = "/trainees/{username}/trainings")
    public ResponseEntity<List<TraineeTrainingResponse>> getTrainingsOfTrainee(
            @Parameter(description = "Trainee username", required = true)
            @PathVariable String username,
            @Parameter(description = "Criteria used to filter trainee trainings", required = true)
            @RequestBody GetTraineeTrainingsRestRequest request,
            @Parameter(hidden = true)
            Authentication authentication
    ){
        userLogger.log(authentication.getName(), "getting all training for user: {}", username);

        try{
            List<TraineeTrainingResponse> trainings = trainingService.getTrainingsByTraineeUsernameAndCriteria(username, request);
            return new ResponseEntity<>(trainings, HttpStatus.OK);
        } catch (InvalidRequestException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Get trainer trainings",
            description = "Returns trainings for a trainer using the supplied search criteria"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Trainer trainings retrieved successfully",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = TrainerTrainingResponse.class)
                    ))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid training search criteria"),
            @ApiResponse(responseCode = "403", description = "Authentication required")
    })
    @GetMapping(value = "/trainers/{username}/trainings")
    public ResponseEntity<List<TrainerTrainingResponse>> getTrainingsOfTrainer(
            @Parameter(description = "Trainer username", required = true)
            @PathVariable("username") String username,
            @Parameter(description = "Criteria used to filter trainer trainings", required = true)
            @RequestBody GetTrainerTrainingsRestRequest request,
            @Parameter(hidden = true)
            Authentication authentication
    ){
        userLogger.log(authentication.getName(), "getting training of trainer: {}", username);

        try{
            List<TrainerTrainingResponse> trainings = trainingService.getTrainingsByTrainerUsernameAndCriteria(username, request);
            return new ResponseEntity<>(trainings, HttpStatus.OK);
        } catch( InvalidRequestException e ){
            return ResponseEntity.badRequest().build();
        }

    }





}
