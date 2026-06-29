package com.elioth.epam.gymcrm.service;

import com.elioth.epam.gymcrm.auth.AuthSession;
import com.elioth.epam.gymcrm.auth.Role;
import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.User;
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
    public AuthSession loginTrainee(String username, String password) {
        validateCredentials(username, password);
        LOG.info("Logging in trainee with username: {}", username);

        Trainee trainee = traineeRepository.findByUserUsernameAndUserPassword(username, password)
                .orElseThrow(() -> new EntityNotFoundException("Username or password incorrect"));

        User user = trainee.getUser();
        ensureActive(user, "Trainee account is not active");

        return new AuthSession(trainee.getTraineeId(), user.getUsername(), Role.TRAINEE);
    }

    @Transactional(readOnly = true)
    public AuthSession loginTrainer(String username, String password) {
        validateCredentials(username, password);
        LOG.info("Logging in trainer with username: {}", username);

        Trainer trainer = trainerRepository.findByUserUsernameAndUserPassword(username, password)
                .orElseThrow(() -> new EntityNotFoundException("Username or password incorrect"));

        User user = trainer.getUser();
        ensureActive(user, "Trainer account is not active");

        return new AuthSession(trainer.getTrainerId(), user.getUsername(), Role.TRAINER);
    }

    private void validateCredentials(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new InvalidRequestException("Invalid username");
        }
        if (password == null || password.isBlank()) {
            throw new InvalidRequestException("Invalid password");
        }
    }

    private void ensureActive(User user, String message) {
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new InvalidRequestException(message);
        }
    }
}
