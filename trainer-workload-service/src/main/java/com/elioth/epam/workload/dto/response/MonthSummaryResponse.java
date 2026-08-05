package com.elioth.epam.workload.dto.response;

public record MonthSummaryResponse(
        int month,
        long trainingSummaryDurationMinutes
) {
}