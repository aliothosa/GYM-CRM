package com.elioth.epam.gymcrm.health;

import com.elioth.epam.gymcrm.repository.TraineeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TraineeRepositoryHealthIndicator implements HealthIndicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeRepositoryHealthIndicator.class);

    private final TraineeRepository traineeRepository;

    public TraineeRepositoryHealthIndicator(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Override
    public Health health() {
        try {
            return Health.up().withDetail("count", traineeRepository.count()).build();
        } catch (Exception exception) {
            LOGGER.warn("Trainee repository health check failed: {}", exception.getClass().getSimpleName());
            return Health.down().build();
        }
    }
}
