package com.elioth.epam.workload.dto.response;

import java.util.List;

public record YearSummaryResponse(
        int year,
        List<MonthSummaryResponse> months
) {
}