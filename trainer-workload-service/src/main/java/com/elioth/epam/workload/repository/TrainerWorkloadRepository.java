package com.elioth.epam.workload.repository;

import com.elioth.epam.workload.persistence.TrainerWorkloadDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerWorkloadRepository extends MongoRepository<TrainerWorkloadDocument, String> {

    Optional<TrainerWorkloadDocument> findByTrainerUsername(String trainerUsername);
}
