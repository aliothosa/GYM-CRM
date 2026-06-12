package com.elioth.epam.gymcrm.service;

import com.elioth.epam.gymcrm.dao.TraineeDao;
import com.elioth.epam.gymcrm.dao.TrainerDao;
import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.exception.InvalidEntityException;
import com.elioth.epam.gymcrm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TraineeService {

    private static final Logger LOG = LoggerFactory.getLogger(TraineeService.class);

    private TraineeDao dao;

    @Autowired
    public void setDao(TraineeDao dao){
        this.dao = dao;
    }

    public Trainee createProfile(Trainee trainee) {
        LOG.info("Creating trainee profile");

        validateTraineeForCreate(trainee);

        trainee.setId(UUID.randomUUID());
        trainee.setUsername(Utils.usernameGenerator(trainee, dao.findAll()));
        trainee.setPassword(Utils.generateRandomPassword());
        trainee.setActive(true);

        Trainee createdTrainee = dao.create(trainee);

        LOG.info("Created trainee profile with id: {}", createdTrainee.getId());

        return createdTrainee;
    }

    public Trainee updateProfile(Trainee trainee) {
        LOG.info("Updating trainee profile with id: {}", trainee != null ? trainee.getId() : null);

        validateTraineeForUpdate(trainee);

        Trainee existingTrainee = dao.findById(trainee.getId())
                .orElseThrow(() -> new EntityNotFoundException("Trainee", trainee.getId()));

        trainee.setUsername(existingTrainee.getUsername());
        trainee.setPassword(existingTrainee.getPassword());
        trainee.setActive(existingTrainee.isActive());

        Trainee updatedTrainee = dao.update(trainee);

        if (updatedTrainee == null) {
            throw new EntityNotFoundException("Trainee", trainee.getId());
        }

        LOG.info("Updated trainee profile with id: {}", updatedTrainee.getId());

        return updatedTrainee;
    }

    public void deleteProfile(UUID traineeId) {
        LOG.info("Deleting trainee profile with id: {}", traineeId);

        if (traineeId == null) {
            throw new InvalidEntityException("Trainee id cannot be null");
        }

        dao.findById(traineeId)
                .orElseThrow(() -> new EntityNotFoundException("Trainee", traineeId));

        dao.delete(traineeId);

        LOG.info("Deleted trainee profile with id: {}", traineeId);
    }

    public Trainee getProfileById(UUID traineeId) {
        LOG.info("Getting trainee profile by id: {}", traineeId);

        if (traineeId == null) {
            throw new InvalidEntityException("Trainee id cannot be null");
        }

        return dao.findById(traineeId)
                .orElseThrow(() -> new EntityNotFoundException("Trainee", traineeId));
    }

    public List<Trainee> getAllProfiles() {
        LOG.info("Getting all trainee profiles");

        return dao.findAll();
    }

    public Trainee getProfileByUsername(String username) {
        LOG.info("Getting trainee profile by username: {}", username);

        if (username == null || username.isBlank()) {
            throw new InvalidEntityException("Username cannot be empty");
        }

        return dao.findAll()
                .stream()
                .filter(trainee -> username.equals(trainee.getUsername()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Trainee with username " + username + " not found"
                ));
    }

    private void validateTraineeForCreate(Trainee trainee) {
        if (trainee == null) {
            throw new InvalidEntityException("Trainee cannot be null");
        }

        validateRequiredFields(trainee);
    }

    private void validateTraineeForUpdate(Trainee trainee) {
        if (trainee == null) {
            throw new InvalidEntityException("Trainee cannot be null");
        }

        if (trainee.getId() == null) {
            throw new InvalidEntityException("Trainee id cannot be null");
        }

        validateRequiredFields(trainee);
    }

    private void validateRequiredFields(Trainee trainee) {
        if (trainee.getFirstName() == null || trainee.getFirstName().isBlank()) {
            throw new InvalidEntityException("First name is required");
        }

        if (trainee.getLastName() == null || trainee.getLastName().isBlank()) {
            throw new InvalidEntityException("Last name is required");
        }
    }
}