package com.elioth.epam.gymcrm.repository;

import com.elioth.epam.gymcrm.domain.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByUserUsername(String username);

    Optional<Trainer> findByUserUsernameAndUserPassword(String username, String password);

    List<Trainer> findAllBySpecializationName(String trainingTypeName);

    @Query("""
            select trainer
            from Trainer trainer
            where trainer.trainerId not in (
                select assignedTrainer.trainerId
                from Trainee trainee
                join trainee.trainers assignedTrainer
                where trainee.user.username = :username
            )
            """)
    List<Trainer> findTrainersNotAssignedToTrainee(@Param("username") String username);
}
