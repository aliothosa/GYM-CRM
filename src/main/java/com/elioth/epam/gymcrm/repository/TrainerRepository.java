package com.elioth.epam.gymcrm.repository;

import com.elioth.epam.gymcrm.domain.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerRepository extends JpaRepository<Trainer,Long> {
}
