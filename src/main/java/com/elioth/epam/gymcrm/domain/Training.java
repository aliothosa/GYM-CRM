package com.elioth.epam.gymcrm.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@ToString
public class Training {
    private long id;
    private UUID traineeId;
    private UUID trainerId;
    private TrainingType type;
    private String name;
    private Date date;
    private int durationInMinutes;
}
