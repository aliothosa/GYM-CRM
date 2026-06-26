package com.elioth.epam.gymcrm.repository;

import com.elioth.epam.gymcrm.domain.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer,Long> {
    Optional<Trainer> findByUserUsername(String username);
    List<Trainer> findAllBySpecializationName(String trainingTypeName);
    @Query("""
        select trainer
        from Trainer trainer
        where trainer.trainerId not in (
            select trainer2.trainerId
            from Trainer trainer2
            join trainer2.trainees trainee
            where trainee.user.username = :username
        )
        """)
    List<Trainer> findTrainersNotAssignedToTrainee(@Param("username") String username);
    void deleteByUserUsername(String username);

    Optional<Trainer> findByUserUsernameAndUserPassword(String username, String password);
}
