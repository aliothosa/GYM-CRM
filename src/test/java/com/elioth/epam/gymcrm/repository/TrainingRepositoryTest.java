package com.elioth.epam.gymcrm.repository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
@Disabled("Requires H2 or dedicated test database configuration; enable after test profile is set up.")
class TrainingRepositoryTest {

    @Autowired
    private TrainingRepository trainingRepository;

    @Test
    void findAllByTraineeTraineeId_shouldReturnTrainings_whenTraineeHasSessions() {
        // TODO: persist Trainee (id known) with two Training rows and one for another trainee
        // TODO: call trainingRepository.findAllByTraineeTraineeId(traineeId)
        // TODO: assert list size 2, all belong to the target trainee
    }

    @Test
    void findAllByTraineeTraineeId_shouldReturnEmptyList_whenTraineeHasNoSessions() {
        // TODO: persist Trainee with no trainings; use unknown or empty traineeId
        // TODO: call trainingRepository.findAllByTraineeTraineeId(traineeId)
        // TODO: assert list is empty
    }

    @Test
    void findAllByTrainerTrainerId_shouldReturnTrainings_whenTrainerHasSessions() {
        // TODO: persist Trainer (id known) linked to two Training rows and one for another trainer
        // TODO: call trainingRepository.findAllByTrainerTrainerId(trainerId)
        // TODO: assert list size 2, all belong to the target trainer
    }

    @Test
    void findAllByTrainerTrainerId_shouldReturnEmptyList_whenTrainerHasNoSessions() {
        // TODO: persist Trainer with no trainings; use unknown or empty trainerId
        // TODO: call trainingRepository.findAllByTrainerTrainerId(trainerId)
        // TODO: assert list is empty
    }

    @Test
    void findAllByTypeName_shouldReturnTrainings_whenTypeMatches() {
        // TODO: persist TrainingType "Yoga" with two trainings; one training with type "Cardio"
        // TODO: call trainingRepository.findAllByTypeName("Yoga")
        // TODO: assert list size 2, all have type.name "Yoga"
    }

    @Test
    void findAllByTypeName_shouldReturnEmptyList_whenTypeNotFound() {
        // TODO: persist trainings with types "Yoga" and "Cardio" only
        // TODO: call trainingRepository.findAllByTypeName("Pilates")
        // TODO: assert list is empty
    }

    @Test
    void findAllByDateBetween_shouldReturnTrainings_withinInclusiveRange() {
        // TODO: persist trainings on 2024-01-01, 2024-01-15, 2024-02-01
        // TODO: call trainingRepository.findAllByDateBetween(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31))
        // TODO: assert list size 2, dates within range
    }

    @Test
    void findAllByDateBetween_shouldReturnEmptyList_whenNoTrainingsInRange() {
        // TODO: persist trainings outside the query range only
        // TODO: call trainingRepository.findAllByDateBetween(from, to) with non-overlapping range
        // TODO: assert list is empty
    }

    @Test
    void findByTraineeUsernameAndCriteria_shouldReturnMatchingTrainings_whenFiltersApplied() {
        // TODO: persist trainee user "john.doe", trainer "Jane Smith", type "Yoga", trainings on varied dates
        // TODO: call findByTraineeUsernameAndCriteria("john.doe", fromDate, toDate, "Jane", "Yoga")
        // TODO: assert returned trainings match username, date range, trainer name fragment, and type (case-insensitive)
    }

    @Test
    void findByTraineeUsernameAndCriteria_shouldReturnEmptyList_whenNoTrainingsMatchCriteria() {
        // TODO: persist trainings for "john.doe" that fail one criterion (e.g. wrong type or date)
        // TODO: call findByTraineeUsernameAndCriteria with restrictive filters
        // TODO: assert list is empty; edge case: all nullable criteria null still scoped to trainee username
    }

    @Test
    void findByTrainerUsernameAndCriteria_shouldReturnMatchingTrainings_whenFiltersApplied() {
        // TODO: persist trainer user "jane.trainer", trainee "John Doe", trainings on varied dates
        // TODO: call findByTrainerUsernameAndCriteria("jane.trainer", fromDate, toDate, "John")
        // TODO: assert returned trainings match trainer username, date range, and trainee name fragment
    }

    @Test
    void findByTrainerUsernameAndCriteria_shouldReturnEmptyList_whenNoTrainingsMatchCriteria() {
        // TODO: persist trainings for "jane.trainer" that fail date or trainee-name filter
        // TODO: call findByTrainerUsernameAndCriteria with restrictive filters
        // TODO: assert list is empty; edge case: traineeName null returns all trainer sessions in date range
    }
}
