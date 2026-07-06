package com.elioth.epam.gymcrm.repository;

import com.elioth.epam.gymcrm.domain.TrainingType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class TrainingTypeRepositoryTest {

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    @Test
    void findByName_shouldReturnTrainingType_whenNameExists() {
        TrainingType yoga = new TrainingType();
        yoga.setName("Yoga");
        trainingTypeRepository.save(yoga);

        Optional<TrainingType> result = trainingTypeRepository.findByName("Yoga");

        assertTrue(result.isPresent());
        assertEquals("Yoga", result.get().getName());
    }

    @Test
    void findByName_shouldReturnEmpty_whenNameNotFound() {
        TrainingType yoga = new TrainingType();
        yoga.setName("Yoga");
        trainingTypeRepository.save(yoga);

        Optional<TrainingType> result = trainingTypeRepository.findByName("Pilates");

        assertTrue(result.isEmpty());
    }
}
