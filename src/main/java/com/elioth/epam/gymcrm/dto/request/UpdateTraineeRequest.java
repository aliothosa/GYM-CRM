package com.elioth.epam.gymcrm.dto.request;

public record UpdateTraineeRequest(
    String firstName,
    String lastName,
    Long trainingTypeId
) {}
