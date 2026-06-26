package com.elioth.epam.gymcrm.repository;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee,Long> {
    Optional<Trainee> findByUserUsername(String username);
    void deleteByUserUsername(String username);
    Optional<Trainee> findByUserUsernameAndUserPassword(String username, String password);
}
