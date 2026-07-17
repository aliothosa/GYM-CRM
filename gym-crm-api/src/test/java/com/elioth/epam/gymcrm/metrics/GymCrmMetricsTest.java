package com.elioth.epam.gymcrm.metrics;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GymCrmMetricsTest {

    @Test
    void shouldIncrementAllCreationCounters() {
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
        GymCrmMetrics metrics = new GymCrmMetrics(meterRegistry);

        metrics.incrementTraineesCreated();
        metrics.incrementTraineesCreated();
        metrics.incrementTrainersCreated();
        metrics.incrementTrainingsCreated();
        metrics.incrementTrainingsCreated();
        metrics.incrementTrainingsCreated();

        assertEquals(2.0, meterRegistry.get(GymCrmMetrics.TRAINEES_CREATED).counter().count());
        assertEquals(1.0, meterRegistry.get(GymCrmMetrics.TRAINERS_CREATED).counter().count());
        assertEquals(3.0, meterRegistry.get(GymCrmMetrics.TRAININGS_CREATED).counter().count());
    }
}
