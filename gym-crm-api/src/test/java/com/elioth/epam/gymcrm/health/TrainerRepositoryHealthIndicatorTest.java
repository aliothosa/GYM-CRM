package com.elioth.epam.gymcrm.health;

import com.elioth.epam.gymcrm.repository.TrainerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerRepositoryHealthIndicatorTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Test
    void shouldReportUpWithTrainerCount() {
        when(trainerRepository.count()).thenReturn(5L);

        var health = new TrainerRepositoryHealthIndicator(trainerRepository).health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(5L, health.getDetails().get("count"));
    }

    @Test
    void shouldReportDownWhenTrainerRepositoryFails() {
        when(trainerRepository.count()).thenThrow(new RuntimeException("database unavailable"));

        var health = new TrainerRepositoryHealthIndicator(trainerRepository).health();

        assertEquals(Status.DOWN, health.getStatus());
    }
}
