package com.elioth.epam.gymcrm.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Trainer extends User {
    private TrainingType specialization;

}
