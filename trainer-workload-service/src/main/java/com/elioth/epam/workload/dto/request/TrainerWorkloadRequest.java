package com.elioth.epam.workload.dto.request;

import com.elioth.epam.workload.domain.ActionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TrainerWorkloadRequest(
        @NotBlank @Size(max = 100) String trainerUsername,
        @NotBlank @Size(max = 100) String trainerFirstName,
        @NotBlank @Size(max = 100) String trainerLastName,
        @NotNull Boolean active,
        @NotNull LocalDate trainingDate,
        @NotNull @Positive Long trainingDurationMinutes,
        @NotNull ActionType actionType
) {
}