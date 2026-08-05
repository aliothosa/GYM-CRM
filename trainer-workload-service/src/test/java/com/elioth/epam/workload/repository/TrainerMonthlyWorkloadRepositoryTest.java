package com.elioth.epam.workload.repository;

import com.elioth.epam.workload.persistence.TrainerMonthlyWorkload;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.flyway.enabled=true"
})
class TrainerMonthlyWorkloadRepositoryTest {

    @Autowired
    private TrainerMonthlyWorkloadRepository repository;

    @Test
    void flywaySchemaPersistsAndUniquelyIdentifiesMonthlyWorkloads() {
        repository.saveAndFlush(workload("john.doe", 2026, 8, 60L));

        TrainerMonthlyWorkload stored = repository
                .findByTrainerUsernameAndWorkloadYearAndWorkloadMonth("john.doe", 2026, 8)
                .orElseThrow();
        assertEquals(60L, stored.getTrainingDurationMinutes());

        assertThrows(DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(workload("john.doe", 2026, 8, 20L)));
    }

    private TrainerMonthlyWorkload workload(String username, int year, int month, long duration) {
        TrainerMonthlyWorkload workload = new TrainerMonthlyWorkload();
        workload.setTrainerUsername(username);
        workload.setTrainerFirstName("John");
        workload.setTrainerLastName("Doe");
        workload.setTrainerActive(true);
        workload.setWorkloadYear(year);
        workload.setWorkloadMonth(month);
        workload.setTrainingDurationMinutes(duration);
        return workload;
    }
}
