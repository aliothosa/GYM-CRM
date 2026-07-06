package com.elioth.epam.gymcrm.dto.request;

public record UpdateTrainerRequest(
   String firstName,
   String lastName,
   Long trainingTypeId
) {}
