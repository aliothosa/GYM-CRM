package com.elioth.epam.workload.controller;

import com.elioth.epam.workload.dto.request.TrainerWorkloadRequest;
import com.elioth.epam.workload.dto.response.TrainerWorkloadSummaryResponse;
import com.elioth.epam.workload.service.TrainerWorkloadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/workloads")
@Tag(
        name = "Trainer Workloads",
        description = """
                Operations for registering trainer workload changes and retrieving
                the accumulated monthly workload of a trainer.
                """
)
public class TrainerWorkloadController {

    private final TrainerWorkloadService workloadService;

    public TrainerWorkloadController(TrainerWorkloadService workloadService) {
        this.workloadService = workloadService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "updateTrainerWorkload",
            summary = "Update a trainer's monthly workload",
            description = """
                    Applies a workload operation for a trainer.

                    An ADD operation increases the trainer's accumulated duration
                    for the year and month of the training date.

                    A DELETE operation decreases the accumulated duration.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Trainer workload updated successfully",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Invalid workload request. This can occur when required
                            fields are missing, field values are invalid, or the
                            requested operation would produce an invalid workload.
                            """,
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected internal server error",
                    content = @Content
            )
    })
    public ResponseEntity<Void> updateWorkload(
            @RequestBody(
                    required = true,
                    description = """
                            Trainer information, training date and duration, and the
                            action that must be applied to the monthly workload.
                            """,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = TrainerWorkloadRequest.class
                            ),
                            examples = @ExampleObject(
                                    name = "Add trainer workload",
                                    summary = "Add a training session",
                                    value = """
                                            {
                                              "trainerUsername": "john.doe",
                                              "trainerFirstName": "John",
                                              "trainerLastName": "Doe",
                                              "active": true,
                                              "trainingDate": "2026-08-05",
                                              "trainingDurationMinutes": 60,
                                              "actionType": "ADD"
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @org.springframework.web.bind.annotation.RequestBody
            TrainerWorkloadRequest request
    ) {
        workloadService.applyWorkload(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping(
            value = "/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            operationId = "getTrainerMonthlyWorkload",
            summary = "Get a trainer's monthly workload",
            description = """
                    Retrieves the accumulated workload for the specified trainer,
                    year and month.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Monthly workload retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation =
                                            TrainerWorkloadSummaryResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Invalid request parameters. The username must not be blank,
                            the year must be 1900 or later, and the month must be
                            between 1 and 12.
                            """,
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                            No workload information was found for the specified
                            trainer, year and month.
                            """,
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected internal server error",
                    content = @Content
            )
    })
    public ResponseEntity<TrainerWorkloadSummaryResponse> getMonthlyWorkload(
            @Parameter(
                    description = "Unique username of the trainer",
                    required = true,
                    example = "john.doe",
                    schema = @Schema(minLength = 1)
            )
            @PathVariable
            @NotBlank
            String username,

            @Parameter(
                    description = "Year of the requested workload period",
                    required = true,
                    example = "2026",
                    schema = @Schema(
                            type = "integer",
                            format = "int32",
                            minimum = "1900"
                    )
            )
            @RequestParam
            @Min(1900)
            int year,

            @Parameter(
                    description = "Month of the requested workload period",
                    required = true,
                    example = "8",
                    schema = @Schema(
                            type = "integer",
                            format = "int32",
                            minimum = "1",
                            maximum = "12"
                    )
            )
            @RequestParam
            @Min(1)
            @Max(12)
            int month
    ) {
        return ResponseEntity.ok(
                workloadService.getMonthlySummary(username, year, month)
        );
    }
}
