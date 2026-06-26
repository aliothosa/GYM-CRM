package com.elioth.epam.gymcrm.repository;

import com.elioth.epam.gymcrm.domain.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {
    List<Training> findAllByTraineeTraineeId(Long traineeId);

    List<Training> findAllByTrainerTrainerId(Long trainerId);

    List<Training> findAllByTypeName(String name);

    List<Training> findAllByDateBetween(LocalDate from, LocalDate to);

    @Query("""
            select training
            from Training training
            join training.trainee trainee
            join trainee.user traineeUser
            join training.trainer trainer
            join trainer.user trainerUser
            join training.type trainingType
            where traineeUser.username = :traineeUsername
              and (:fromDate is null or training.date >= :fromDate)
              and (:toDate is null or training.date <= :toDate)
              and (
                    :trainerName is null
                    or lower(concat(trainerUser.firstName, ' ', trainerUser.lastName)) like lower(concat('%', :trainerName, '%'))
                    or lower(trainerUser.firstName) like lower(concat('%', :trainerName, '%'))
                    or lower(trainerUser.lastName) like lower(concat('%', :trainerName, '%'))
              )
              and (:trainingTypeName is null or lower(trainingType.name) = lower(:trainingTypeName))
            """)
    List<Training> findByTraineeUsernameAndCriteria(
            @Param("traineeUsername") String traineeUsername,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("trainerName") String trainerName,
            @Param("trainingTypeName") String trainingTypeName
    );

    @Query("""
            select training
            from Training training
            join training.trainee trainee
            join trainee.user traineeUser
            join training.trainer trainer
            join trainer.user trainerUser
            where trainerUser.username = :trainerUsername
              and (:fromDate is null or training.date >= :fromDate)
              and (:toDate is null or training.date <= :toDate)
              and (
                    :traineeName is null
                    or lower(concat(traineeUser.firstName, ' ', traineeUser.lastName)) like lower(concat('%', :traineeName, '%'))
                    or lower(traineeUser.firstName) like lower(concat('%', :traineeName, '%'))
                    or lower(traineeUser.lastName) like lower(concat('%', :traineeName, '%'))
              )
            """)
    List<Training> findByTrainerUsernameAndCriteria(
            @Param("trainerUsername") String trainerUsername,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("traineeName") String traineeName
    );
}
