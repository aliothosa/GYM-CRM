package com.elioth.epam.gymcrm.repository;

import com.elioth.epam.gymcrm.domain.Training;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingRepository extends JpaRepository<Training, Long> {
}
