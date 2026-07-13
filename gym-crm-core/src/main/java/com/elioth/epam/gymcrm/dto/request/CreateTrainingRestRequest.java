package com.elioth.epam.gymcrm.dto.request;

import java.time.LocalDate;

public record CreateTrainingRestRequest(
    String traineeUsername,
    String trainerUsername,
    String trainingName,
    LocalDate date,
    Long durationInMinutes

){

}