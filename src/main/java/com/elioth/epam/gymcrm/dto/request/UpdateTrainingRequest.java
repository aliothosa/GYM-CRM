package com.elioth.epam.gymcrm.dto.request;

import java.time.LocalDate;

public record UpdateTrainingRequest(
    String name,
    LocalDate date,
    long duration
) {}
