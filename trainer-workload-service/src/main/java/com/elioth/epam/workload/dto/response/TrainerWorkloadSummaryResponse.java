package com.elioth.epam.workload.dto.response;

import java.util.List;

public record TrainerWorkloadSummaryResponse(
        String trainerUsername,
        String trainerFirstName,
        String trainerLastName,
        boolean trainerStatus,
        List<YearSummaryResponse> years
) {
}