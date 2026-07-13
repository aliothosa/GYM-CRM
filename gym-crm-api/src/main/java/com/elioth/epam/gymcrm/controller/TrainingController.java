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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "Trainings", description = "Training management operations")
public class TrainingController {

    private UserLogger userLogger;

    private TrainingService trainingService;

    @Autowired
    public TrainingController(UserLogger userLogger, TrainingService trainingService) {
        this.userLogger = userLogger;
        this.trainingService = trainingService;
    }

    @ApiOperation(
            value = "Add training",
            notes = "Creates a training between an existing trainee and trainer",
            response = Void.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Training created successfully"),
            @ApiResponse(code = 400, message = "Trainee, trainer, or training type not found"),
            @ApiResponse(code = 403, message = "Authentication required"),
            @ApiResponse(code = 404, message = "Invalid training information")
    })
    @PostMapping(value = "/trainings")
    public ResponseEntity<Void> addTraining(
            @ApiParam(value = "Information required to create a training", required = true)
            @RequestBody CreateTrainingRestRequest createTrainingRequest,
            @ApiParam(hidden = true)
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


    @ApiOperation(
            value = "Get training types",
            notes = "Returns all available training type names",
            response = String.class,
            responseContainer = "List"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Training types retrieved successfully"),
            @ApiResponse(code = 403, message = "Authentication required")
    })
    @GetMapping(value = "/trainings/training-types")
    public ResponseEntity<List<String>> getAllTrainingTypes(
            @ApiParam(hidden = true)
            @SessionAttribute("AUTH_SESSION")
            AuthSession authSession
    ) {
        userLogger.log(authSession.username(), "getting all training types");

        List<String> names = trainingService.getTrainingTypeNames();

        return new ResponseEntity<>(names, HttpStatus.OK);
    }


    @ApiOperation(
            value = "Get trainee trainings",
            notes = "Returns trainings for a trainee using the supplied search criteria",
            response = TraineeTrainingResponse.class,
            responseContainer = "List"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee trainings retrieved successfully"),
            @ApiResponse(code = 400, message = "Invalid training search criteria"),
            @ApiResponse(code = 403, message = "Authentication required")
    })
    @GetMapping(value = "/trainees/{username}/trainings")
    public ResponseEntity<List<TraineeTrainingResponse>> getTrainingsOfTrainee(
            @ApiParam(value = "Trainee username", required = true)
            @PathVariable String username,
            @ApiParam(value = "Criteria used to filter trainee trainings", required = true)
            @RequestBody GetTraineeTrainingsRestRequest request,
            @ApiParam(hidden = true)
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

    @ApiOperation(
            value = "Get trainer trainings",
            notes = "Returns trainings for a trainer using the supplied search criteria",
            response = TrainerTrainingResponse.class,
            responseContainer = "List"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainer trainings retrieved successfully"),
            @ApiResponse(code = 400, message = "Invalid training search criteria"),
            @ApiResponse(code = 403, message = "Authentication required")
    })
    @GetMapping(value = "/trainers/{username}/trainings")
    public ResponseEntity<List<TrainerTrainingResponse>> getTrainingsOfTrainer(
            @ApiParam(value = "Trainer username", required = true)
            @PathVariable("username") String username,
            @ApiParam(value = "Criteria used to filter trainer trainings", required = true)
            @RequestBody GetTrainerTrainingsRestRequest request,
            @ApiParam(hidden = true)
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
