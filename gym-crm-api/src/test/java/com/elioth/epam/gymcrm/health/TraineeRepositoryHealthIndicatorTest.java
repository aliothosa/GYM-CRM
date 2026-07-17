package com.elioth.epam.gymcrm.health;

import com.elioth.epam.gymcrm.repository.TraineeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeRepositoryHealthIndicatorTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Test
    void shouldReportUpWithTraineeCount() {
        when(traineeRepository.count()).thenReturn(3L);

        var health = new TraineeRepositoryHealthIndicator(traineeRepository).health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(3L, health.getDetails().get("count"));
    }

    @Test
    void shouldReportDownWhenTraineeRepositoryFails() {
        when(traineeRepository.count()).thenThrow(new RuntimeException("database unavailable"));

        var health = new TraineeRepositoryHealthIndicator(traineeRepository).health();

        assertEquals(Status.DOWN, health.getStatus());
    }
}
