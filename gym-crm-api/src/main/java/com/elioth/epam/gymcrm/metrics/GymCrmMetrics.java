package com.elioth.epam.gymcrm.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class GymCrmMetrics {

    public static final String TRAINEES_CREATED = "gymcrm.trainees.created";
    public static final String TRAINERS_CREATED = "gymcrm.trainers.created";
    public static final String TRAININGS_CREATED = "gymcrm.trainings.created";

    private final Counter traineesCreated;
    private final Counter trainersCreated;
    private final Counter trainingsCreated;

    public GymCrmMetrics(MeterRegistry meterRegistry) {
        traineesCreated = Counter.builder(TRAINEES_CREATED).register(meterRegistry);
        trainersCreated = Counter.builder(TRAINERS_CREATED).register(meterRegistry);
        trainingsCreated = Counter.builder(TRAININGS_CREATED).register(meterRegistry);
    }

    public void incrementTraineesCreated() {
        traineesCreated.increment();
    }

    public void incrementTrainersCreated() {
        trainersCreated.increment();
    }

    public void incrementTrainingsCreated() {
        trainingsCreated.increment();
    }
}
