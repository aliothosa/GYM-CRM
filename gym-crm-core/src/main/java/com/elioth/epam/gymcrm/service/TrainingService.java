package com.elioth.epam.gymcrm.service;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.Training;
import com.elioth.epam.gymcrm.domain.TrainingType;
import com.elioth.epam.gymcrm.dto.mapper.TrainingMapper;
import com.elioth.epam.gymcrm.dto.request.CreateTrainingRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTrainingRequest;
import com.elioth.epam.gymcrm.dto.response.TrainingResponse;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.exception.InvalidEntityException;
import com.elioth.epam.gymcrm.exception.InvalidRequestException;
import com.elioth.epam.gymcrm.repository.TraineeRepository;
import com.elioth.epam.gymcrm.repository.TrainerRepository;
import com.elioth.epam.gymcrm.repository.TrainingRepository;
import com.elioth.epam.gymcrm.repository.TrainingTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TrainingService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainingService.class);

    private TraineeRepository traineeRepository;
    private TrainerRepository trainerRepository;
    private TrainingRepository trainingRepository;
    private TrainingTypeRepository trainingTypeRepository;

    @Autowired
    public void setTrainingRepository(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @Autowired
    public void setTraineeRepository(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Autowired
    public void setTrainingTypeRepository(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Transactional
    public TrainingResponse createTraining(Long trainerId, CreateTrainingRequest request) {
        LOG.info("Creating training for trainer id: {}", trainerId);

        validateCreateRequest(request);

        if (!trainerId.equals(request.trainerId())) {
            throw new InvalidRequestException("Trainer id must match the authenticated trainer");
        }

        Trainee fetchedTrainee = findTraineeByIdOrThrow(request.traineeId());
        Trainer fetchedTrainer = findTrainerByIdOrThrow(request.trainerId());
        TrainingType fetchedTrainingType = findTrainingTypeByIdOrThrow(request.trainingTypeId());

        Training training = TrainingMapper.toEntity(request, fetchedTrainee, fetchedTrainer, fetchedTrainingType);
        Training savedTraining = trainingRepository.save(training);

        LOG.info("Created training with id: {}", savedTraining.getTrainingId());

        return TrainingMapper.toResponse(savedTraining);
    }

    @Transactional
    public TrainingResponse updateTraining(Long trainerId, Long trainingId, UpdateTrainingRequest request) {
        LOG.info("Updating training with id: {} for trainer id: {}", trainingId, trainerId);

        validateUpdateRequest(request);

        Training fetchedTraining = findTrainingByIdOrThrow(trainingId);
        assertTrainingOwnedByTrainer(fetchedTraining, trainerId);

        TrainingMapper.updateEntity(fetchedTraining, request);

        return TrainingMapper.toResponse(fetchedTraining);
    }

    @Transactional
    public void deleteTraining(Long trainerId, Long trainingId) {
        LOG.info("Deleting training with id: {} for trainer id: {}", trainingId, trainerId);

        Training fetchedTraining = findTrainingByIdOrThrow(trainingId);
        assertTrainingOwnedByTrainer(fetchedTraining, trainerId);

        trainingRepository.delete(fetchedTraining);
    }

    @Transactional(readOnly = true)
    public TrainingResponse getTrainingById(Long trainerId, Long trainingId) {
        LOG.info("Getting training by id: {} for trainer id: {}", trainingId, trainerId);

        Training fetchedTraining = findTrainingByIdOrThrow(trainingId);
        assertTrainingOwnedByTrainer(fetchedTraining, trainerId);

        return TrainingMapper.toResponse(fetchedTraining);
    }

    @Transactional(readOnly = true)
    public List<TrainingResponse> getTrainingsByTraineeUsernameAndCriteria(
            String traineeUsername,
            LocalDate from,
            LocalDate to,
            String trainerName,
            String trainingTypeName
    ) {
        LOG.info(
                "Getting trainings for trainee username: {} with criteria fromDate={}, toDate={}, trainerName={}, trainingType={}",
                traineeUsername,
                from,
                to,
                trainerName,
                trainingTypeName
        );

        return trainingRepository.findByTraineeUsernameAndCriteria(
                        traineeUsername,
                        from,
                        to,
                        trainerName,
                        trainingTypeName != null ? trainingTypeName.toUpperCase() : null
                )
                .stream()
                .map(TrainingMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TrainingResponse> getTrainingsByTrainerUsernameAndCriteria(
            String trainerUsername,
            LocalDate from,
            LocalDate to,
            String traineeName
    ) {
        LOG.info(
                "Getting trainings for trainer username: {} with criteria fromDate={}, toDate={}, traineeName={}",
                trainerUsername,
                from,
                to,
                traineeName
        );

        return trainingRepository.findByTrainerUsernameAndCriteria(trainerUsername, from, to, traineeName)
                .stream()
                .map(TrainingMapper::toResponse)
                .toList();
    }

    private void assertTrainingOwnedByTrainer(Training training, Long trainerId) {
        if (!trainerId.equals(training.getTrainer().getTrainerId())) {
            throw new InvalidRequestException("Trainer cannot access this training");
        }
    }

    private void validateCreateRequest(CreateTrainingRequest request) {
        if (request == null) {
            throw new InvalidEntityException("Request cannot be null");
        }
        if (request.traineeId() == null) {
            throw new InvalidEntityException("Trainee id cannot be null");
        }
        if (request.trainerId() == null) {
            throw new InvalidEntityException("Trainer id cannot be null");
        }
        if (request.trainingTypeId() == null) {
            throw new InvalidEntityException("Training type id cannot be null");
        }
        if (request.trainingName() == null || request.trainingName().isBlank()) {
            throw new InvalidEntityException("Training name cannot be empty");
        }
        if (request.trainingDate() == null) {
            throw new InvalidEntityException("Training date cannot be null");
        }
        if (request.duration() == null || request.duration() <= 0) {
            throw new InvalidEntityException("Training duration must be greater than 0");
        }
    }

    private void validateUpdateRequest(UpdateTrainingRequest request) {
        if (request == null) {
            throw new InvalidEntityException("Request cannot be null");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new InvalidEntityException("Training name cannot be empty");
        }
        if (request.duration() == null || request.duration() <= 0) {
            throw new InvalidEntityException("Training duration must be greater than 0");
        }
        if (request.date() == null) {
            throw new InvalidEntityException("Training date cannot be null");
        }
    }

    private Training findTrainingByIdOrThrow(Long id) {
        return trainingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Training with id " + id + " not found"));
    }

    private Trainee findTraineeByIdOrThrow(Long id) {
        return traineeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainee with id " + id + " not found"));
    }

    private Trainer findTrainerByIdOrThrow(Long id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainer with id " + id + " not found"));
    }

    private TrainingType findTrainingTypeByIdOrThrow(Long id) {
        return trainingTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Training type with id " + id + " not found"));
    }
}
