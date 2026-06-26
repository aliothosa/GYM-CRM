package com.elioth.epam.gymcrm.service;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.exception.InvalidRequestException;
import com.elioth.epam.gymcrm.repository.TraineeRepository;
import com.elioth.epam.gymcrm.repository.TrainerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);

    private TraineeRepository traineeRepository;
    private TrainerRepository trainerRepository;

    @Autowired
    public void setTraineeRepository(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Transactional(readOnly = true)
    public Trainee authenticateTrainee(String username, String password) {
        validateCredentials(username, password);
        LOG.info("Authenticating trainee with username: {}", username);

        return traineeRepository.findByUserUsernameAndUserPassword(username, password)
                .orElseThrow(() -> new EntityNotFoundException("Username or password incorrect"));
    }

    @Transactional(readOnly = true)
    public Trainer authenticateTrainer(String username, String password) {
        validateCredentials(username, password);
        LOG.info("Authenticating trainer with username: {}", username);

        return trainerRepository.findByUserUsernameAndUserPassword(username, password)
                .orElseThrow(() -> new EntityNotFoundException("Username or password incorrect"));
    }

    private void validateCredentials(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new InvalidRequestException("Invalid username");
        }
        if (password == null || password.isBlank()) {
            throw new InvalidRequestException("Invalid password");
        }
    }
}
