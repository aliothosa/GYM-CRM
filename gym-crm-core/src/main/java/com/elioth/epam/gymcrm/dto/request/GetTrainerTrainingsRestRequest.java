package com.elioth.epam.gymcrm.dto.request;

import java.time.LocalDate;

public record GetTrainerTrainingsRestRequest(
        LocalDate periodFrom,
        LocalDate periodTo,
        String traineeName
) {
}
