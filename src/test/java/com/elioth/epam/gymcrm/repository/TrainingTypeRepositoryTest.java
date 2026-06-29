package com.elioth.epam.gymcrm.repository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
@Disabled("Requires H2 or dedicated test database configuration; enable after test profile is set up.")
class TrainingTypeRepositoryTest {

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    @Test
    void findByName_shouldReturnTrainingType_whenNameExists() {
        // TODO: persist TrainingType with name "Yoga"
        // TODO: call trainingTypeRepository.findByName("Yoga")
        // TODO: assert Optional.isPresent(), name equals "Yoga"
    }

    @Test
    void findByName_shouldReturnEmpty_whenNameNotFound() {
        // TODO: persist TrainingType "Yoga" only (or empty database)
        // TODO: call trainingTypeRepository.findByName("Pilates")
        // TODO: assert Optional.isEmpty()
    }
}
