package com.elioth.epam.gymcrm.controller;

import com.elioth.epam.gymcrm.dto.request.CreateTrainingRestRequest;
import com.elioth.epam.gymcrm.dto.request.GetTraineeTrainingsRestRequest;
import com.elioth.epam.gymcrm.dto.request.GetTrainerTrainingsRestRequest;
import com.elioth.epam.gymcrm.dto.response.TraineeTrainingResponse;
import com.elioth.epam.gymcrm.dto.response.TrainerTrainingResponseWithID;
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
import org.springframework.http.MediaType;
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

    @GetMapping(
            value = "/trainers/{username}/trainings-simple",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            operationId = "getTrainerTrainingsSimple",
            summary = "Get a trainer's trainings",
            description = """
                Retrieves a simplified list of the training sessions assigned to the
                specified trainer. Each item includes the training ID, which can be
                used to delete a specific training session.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Training sessions retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = TrainerTrainingResponseWithID.class
                                    )
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "The trainer username is invalid",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication is required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "The authenticated user is not authorized to access this resource",
                    content = @Content
            )
    })
    public ResponseEntity<List<TrainerTrainingResponseWithID>> getTrainerTrainings(
            @Parameter(
                    description = "Unique username of the trainer whose trainings will be retrieved",
                    required = true,
                    example = "john.doe"
            )
            @PathVariable String username,

            @Parameter(hidden = true)
            Authentication authentication
    ) {
        userLogger.log(
                authentication.getName(),
                "Getting trainings for trainer: {}",
                username
        );

        try {
            List<TrainerTrainingResponseWithID> trainings =
                    trainingService.getTrainingsForTrainer(username);

            return ResponseEntity.ok(trainings);
        } catch (InvalidRequestException exception) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/trainers/{username}/trainings/{trainingId}")
    @Operation(
            operationId = "deleteTrainerTraining",
            summary = "Delete a trainer's training",
            description = """
                Deletes a specific training session assigned to the specified trainer.
                The training ID can be obtained from the trainer trainings endpoint.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Training session deleted successfully",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "The trainer username or training ID is invalid",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication is required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "The authenticated user is not authorized to delete this training",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "The trainer or training session was not found",
                    content = @Content
            )
    })
    public ResponseEntity<Void> deleteTrainerTraining(
            @Parameter(
                    description = "Unique username of the trainer associated with the training",
                    required = true,
                    example = "john.doe"
            )
            @PathVariable String username,

            @Parameter(
                    description = """
                        Unique ID of the training session to delete. The ID can be obtained
                        from GET /trainers/{username}/trainings-simple.
                        """,
                    required = true,
                    example = "42",
                    schema = @Schema(
                            type = "integer",
                            format = "int64",
                            minimum = "1"
                    )
            )
            @PathVariable Long trainingId,

            @Parameter(hidden = true)
            Authentication authentication
    ) {
        userLogger.log(
                authentication.getName(),
                "Deleting training {} for trainer: {}",
                trainingId,
                username
        );

        try {
            trainingService.deleteTraining(username, trainingId);

            return ResponseEntity.noContent().build();
        } catch (InvalidRequestException exception) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }





}
