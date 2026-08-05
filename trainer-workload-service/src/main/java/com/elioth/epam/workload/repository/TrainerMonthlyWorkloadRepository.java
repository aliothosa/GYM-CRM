package com.elioth.epam.workload.repository;

import com.elioth.epam.workload.persistence.TrainerMonthlyWorkload;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerMonthlyWorkloadRepository extends JpaRepository<TrainerMonthlyWorkload, Long> {

    Optional<TrainerMonthlyWorkload> findByTrainerUsernameAndWorkloadYearAndWorkloadMonth(String username, int workloadYear, int workloadMonth);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TrainerMonthlyWorkload> findForUpdateByTrainerUsernameAndWorkloadYearAndWorkloadMonth(String username, int workloadYear, int workloadMonth);

}
