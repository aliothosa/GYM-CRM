package com.elioth.epam.gymcrm.service;

import com.elioth.epam.gymcrm.dao.TrainerDao;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.TrainingType;
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
public class TrainerService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainerService.class);

    private  TrainerDao dao;

    @Autowired
    public void setTrainerDao(TrainerDao dao) {
        this.dao = dao;
    }

    public Trainer createProfile(Trainer trainer) {
        LOG.info("Creating trainer profile");

        validateTrainerForCreate(trainer);

        trainer.setId(UUID.randomUUID());
        trainer.setUsername(Utils.usernameGenerator(trainer, dao.findAll()));
        trainer.setPassword(Utils.generateRandomPassword());
        trainer.setActive(true);

        Trainer createdTrainer = dao.create(trainer);

        LOG.info("Created trainer profile with id: {}", createdTrainer.getId());

        return createdTrainer;
    }

    public Trainer updateProfile(Trainer trainer) {
        LOG.info("Updating trainer profile with id: {}", trainer != null ? trainer.getId() : null);

        validateTrainerForUpdate(trainer);

        Trainer existingTrainer = dao.findById(trainer.getId())
                .orElseThrow(() -> new EntityNotFoundException("Trainer", trainer.getId()));

        trainer.setUsername(existingTrainer.getUsername());
        trainer.setPassword(existingTrainer.getPassword());
        trainer.setActive(existingTrainer.isActive());

        Trainer updatedTrainer = dao.update(trainer);

        if (updatedTrainer == null) {
            throw new EntityNotFoundException("Trainer", trainer.getId());
        }

        LOG.info("Updated trainer profile with id: {}", updatedTrainer.getId());

        return updatedTrainer;
    }

    public void deleteProfile(UUID trainerId) {
        LOG.info("Deleting trainer profile with id: {}", trainerId);

        if (trainerId == null) {
            throw new InvalidEntityException("Trainer id cannot be null");
        }

        dao.findById(trainerId)
                .orElseThrow(() -> new EntityNotFoundException("Trainer", trainerId));

        dao.delete(trainerId);

        LOG.info("Deleted trainer profile with id: {}", trainerId);
    }

    public Trainer getProfileById(UUID trainerId) {
        LOG.info("Getting trainer profile by id: {}", trainerId);

        if (trainerId == null) {
            throw new InvalidEntityException("Trainer id cannot be null");
        }

        return dao.findById(trainerId)
                .orElseThrow(() -> new EntityNotFoundException("Trainer", trainerId));
    }

    public List<Trainer> getAllProfiles() {
        LOG.info("Getting all trainer profiles");

        return dao.findAll();
    }

    public Trainer getProfileByUsername(String username) {
        LOG.info("Getting trainer profile by username: {}", username);

        if (username == null || username.isBlank()) {
            throw new InvalidEntityException("Username cannot be empty");
        }

        return dao.findAll()
                .stream()
                .filter(trainer -> username.equals(trainer.getUsername()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Trainer with username " + username + " not found"
                ));
    }

    public List<Trainer> findBySpecialization(TrainingType specialization) {
        LOG.info("Getting trainers by specialization: {}", specialization);

        if (specialization == null) {
            throw new InvalidEntityException("Specialization cannot be null");
        }

        return dao.findAll()
                .stream()
                .filter(trainer -> specialization.equals(trainer.getSpecialization()))
                .toList();
    }

    private void validateTrainerForCreate(Trainer trainer) {
        if (trainer == null) {
            throw new InvalidEntityException("Trainer cannot be null");
        }

        validateRequiredFields(trainer);
    }

    private void validateTrainerForUpdate(Trainer trainer) {
        if (trainer == null) {
            throw new InvalidEntityException("Trainer cannot be null");
        }

        if (trainer.getId() == null) {
            throw new InvalidEntityException("Trainer id cannot be null");
        }

        validateRequiredFields(trainer);
    }

    private void validateRequiredFields(Trainer trainer) {
        if (trainer.getFirstName() == null || trainer.getFirstName().isBlank()) {
            throw new InvalidEntityException("First name is required");
        }

        if (trainer.getLastName() == null || trainer.getLastName().isBlank()) {
            throw new InvalidEntityException("Last name is required");
        }

        if (trainer.getSpecialization() == null) {
            throw new InvalidEntityException("Specialization is required");
        }
    }
}