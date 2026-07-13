package com.elioth.epam.gymcrm.dto.response;

import com.elioth.epam.gymcrm.domain.Trainee;

import java.util.List;
import java.util.Set;

public record TrainerResponse(
        Long trainerId,
        Long userId,
        String firstName,
        String lastName,
        String username,
        Boolean active,
        Long trainingTypeId,
        String trainingTypeName,
        List<EmbeddedTraineeResponse> trainees
) {}
