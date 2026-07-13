package com.elioth.epam.gymcrm.dto.response;

import java.time.LocalDate;

/*Training Name
II. Training Date
III. Training Type
IV. Training Duration
V. Trainer Name*/
public record TraineeTrainingResponse(
        String name,
        LocalDate date,
        String trainingType,
        Long duration,
        String trainerName
) {
}
