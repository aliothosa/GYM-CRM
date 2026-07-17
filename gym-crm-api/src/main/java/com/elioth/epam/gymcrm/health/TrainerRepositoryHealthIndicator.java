package com.elioth.epam.gymcrm.health;

import com.elioth.epam.gymcrm.repository.TrainerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TrainerRepositoryHealthIndicator implements HealthIndicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerRepositoryHealthIndicator.class);

    private final TrainerRepository trainerRepository;

    public TrainerRepositoryHealthIndicator(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Override
    public Health health() {
        try {
            return Health.up().withDetail("count", trainerRepository.count()).build();
        } catch (Exception exception) {
            LOGGER.warn("Trainer repository health check failed: {}", exception.getClass().getSimpleName());
            return Health.down().build();
        }
    }
}
