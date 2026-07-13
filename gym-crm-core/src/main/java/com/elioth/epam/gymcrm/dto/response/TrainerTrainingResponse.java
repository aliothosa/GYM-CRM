package com.elioth.epam.gymcrm.dto.response;

import java.time.LocalDate;

/*. Training Name
II. Training Date
III. Training Type
IV. Training Duration
V. Trainee Name*/
public record TrainerTrainingResponse(
        String name,
        LocalDate date,
        String trainingType,
        Long duration,
        String traineeName
) {
}
