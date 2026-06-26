package com.elioth.epam.gymcrm.repository;

import com.elioth.epam.gymcrm.domain.Training;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {
    List<Training> findAllByTraineeTraineeId(Long id);
    List<Training> findAllByTrainerTrainerId(Long id);
    List<Training> findAllByTypeName(String name);
    List<Training> findAllByDateBetween(LocalDate from, LocalDate to);
    List<Training> findAllByDateBetweenAndTraineeUserUsername(LocalDate from, LocalDate to, String username);
    List<Training> findAllByDateBetweenAndTrainerUserUsername(LocalDate from, LocalDate to, String username);

}
