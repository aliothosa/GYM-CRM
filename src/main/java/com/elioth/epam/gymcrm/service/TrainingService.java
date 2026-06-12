package com.elioth.epam.gymcrm.service;

import com.elioth.epam.gymcrm.dao.TraineeDao;
import com.elioth.epam.gymcrm.dao.TrainerDao;
import com.elioth.epam.gymcrm.dao.TrainingDao;
import com.elioth.epam.gymcrm.domain.Training;
import com.elioth.epam.gymcrm.domain.TrainingType;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.exception.InvalidEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class TrainingService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainingService.class);

    private TrainingDao trainingDao;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    public Training createTraining(Training training) {
        LOG.info("Creating training");

        validateTrainingForCreate(training);
        validateRelatedProfilesExist(training);

        training.setId(generateNextId());

        Training createdTraining = trainingDao.create(training);

        LOG.info("Created training with id: {}", createdTraining.getId());

        return createdTraining;
    }

    public Training updateTraining(Training training) {
        LOG.info("Updating training with id: {}", training != null ? training.getId() : null);

        validateTrainingForUpdate(training);
        validateRelatedProfilesExist(training);

        trainingDao.findById(training.getId())
                .orElseThrow(() -> new EntityNotFoundException("Training", training.getId()));

        Training updatedTraining = trainingDao.update(training);

        if (updatedTraining == null) {
            throw new EntityNotFoundException("Training", training.getId());
        }

        LOG.info("Updated training with id: {}", updatedTraining.getId());

        return updatedTraining;
    }

    public void deleteTraining(long trainingId) {
        LOG.info("Deleting training with id: {}", trainingId);

        if (trainingId <= 0) {
            throw new InvalidEntityException("Training id must be greater than 0");
        }

        trainingDao.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException("Training", trainingId));

        trainingDao.delete(trainingId);

        LOG.info("Deleted training with id: {}", trainingId);
    }

    public Training getTrainingById(long trainingId) {
        LOG.info("Getting training by id: {}", trainingId);

        if (trainingId <= 0) {
            throw new InvalidEntityException("Training id must be greater than 0");
        }

        return trainingDao.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException("Training", trainingId));
    }

    public List<Training> getAllTrainings() {
        LOG.info("Getting all trainings");

        return trainingDao.findAll();
    }

    public List<Training> getTrainingsByTraineeId(UUID traineeId) {
        LOG.info("Getting trainings by trainee id: {}", traineeId);

        if (traineeId == null) {
            throw new InvalidEntityException("Trainee id cannot be null");
        }

        return trainingDao.findAll()
                .stream()
                .filter(training -> traineeId.equals(training.getTraineeId()))
                .toList();
    }

    public List<Training> getTrainingsByTrainerId(UUID trainerId) {
        LOG.info("Getting trainings by trainer id: {}", trainerId);

        if (trainerId == null) {
            throw new InvalidEntityException("Trainer id cannot be null");
        }

        return trainingDao.findAll()
                .stream()
                .filter(training -> trainerId.equals(training.getTrainerId()))
                .toList();
    }

    public List<Training> getTrainingsByType(TrainingType type) {
        LOG.info("Getting trainings by type: {}", type);

        if (type == null) {
            throw new InvalidEntityException("Training type cannot be null");
        }

        return trainingDao.findAll()
                .stream()
                .filter(training -> type.equals(training.getType()))
                .toList();
    }

    public List<Training> getTrainingsByDateBetween(Date from, Date to) {
        LOG.info("Getting trainings between dates: {} and {}", from, to);

        if (from == null || to == null) {
            throw new InvalidEntityException("Dates cannot be null");
        }

        if (from.after(to)) {
            throw new InvalidEntityException("Start date cannot be after end date");
        }

        return trainingDao.findAll()
                .stream()
                .filter(training -> isDateBetween(training.getDate(), from, to))
                .toList();
    }

    private void validateTrainingForCreate(Training training) {
        if (training == null) {
            throw new InvalidEntityException("Training cannot be null");
        }

        validateRequiredFields(training);
    }

    private void validateTrainingForUpdate(Training training) {
        if (training == null) {
            throw new InvalidEntityException("Training cannot be null");
        }

        if (training.getId() <= 0) {
            throw new InvalidEntityException("Training id must be greater than 0");
        }

        validateRequiredFields(training);
    }

    private void validateRequiredFields(Training training) {
        if (training.getTraineeId() == null) {
            throw new InvalidEntityException("Trainee id cannot be null");
        }

        if (training.getTrainerId() == null) {
            throw new InvalidEntityException("Trainer id cannot be null");
        }

        if (training.getType() == null) {
            throw new InvalidEntityException("Training type cannot be null");
        }

        if (training.getName() == null || training.getName().isBlank()) {
            throw new InvalidEntityException("Training name cannot be empty");
        }

        if (training.getDate() == null) {
            throw new InvalidEntityException("Training date cannot be null");
        }

        if (training.getDurationInMinutes() <= 0) {
            throw new InvalidEntityException("Training duration must be greater than 0");
        }
    }

    private void validateRelatedProfilesExist(Training training) {
        traineeDao.findById(training.getTraineeId())
                .orElseThrow(() -> new EntityNotFoundException("Trainee", training.getTraineeId()));

        trainerDao.findById(training.getTrainerId())
                .orElseThrow(() -> new EntityNotFoundException("Trainer", training.getTrainerId()));
    }

    private boolean isDateBetween(Date date, Date from, Date to) {
        if (date == null) {
            return false;
        }

        return !date.before(from) && !date.after(to);
    }

    private long generateNextId() {
        return trainingDao.findAll()
                .stream()
                .mapToLong(Training::getId)
                .max()
                .orElse(0L) + 1;
    }
}