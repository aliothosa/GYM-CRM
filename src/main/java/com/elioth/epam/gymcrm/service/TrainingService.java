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
    private AuthService authService;

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

    @Autowired
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @Transactional
    public TrainingResponse createTraining(String trainerUsername, String password, CreateTrainingRequest request) {
        LOG.info("Creating training for trainer username: {}", trainerUsername);

        validateCreateRequest(request);

        Trainer authenticatedTrainer = authService.authenticateTrainer(trainerUsername, password);
        Trainee fetchedTrainee = findTraineeByIdOrThrow(request.traineeId());
        Trainer fetchedTrainer = findTrainerByIdOrThrow(request.trainerId());
        TrainingType fetchedTrainingType = findTrainingTypeByIdOrThrow(request.trainingTypeId());

        if (!authenticatedTrainer.getTrainerId().equals(fetchedTrainer.getTrainerId())) {
            throw new InvalidRequestException("Authenticated trainer must match the training trainer");
        }

        Training training = TrainingMapper.toEntity(request, fetchedTrainee, fetchedTrainer, fetchedTrainingType);
        Training savedTraining = trainingRepository.save(training);

        LOG.info("Created training with id: {}", savedTraining.getTrainingId());

        return TrainingMapper.toResponse(savedTraining);
    }

    @Transactional
    public TrainingResponse updateTraining(String trainerUsername, String password, Long id, UpdateTrainingRequest request) {
        LOG.info("Updating training with id: {} for trainer username: {}", id, trainerUsername);

        validateUpdateRequest(request);

        Trainer authenticatedTrainer = authService.authenticateTrainer(trainerUsername, password);
        Training fetchedTraining = findTrainingByIdOrThrow(id);

        if (!authenticatedTrainer.getTrainerId().equals(fetchedTraining.getTrainer().getTrainerId())) {
            throw new InvalidRequestException("Authenticated trainer cannot update this training");
        }

        TrainingMapper.updateEntity(fetchedTraining, request);

        return TrainingMapper.toResponse(fetchedTraining);
    }

    @Transactional
    public void deleteTraining(String trainerUsername, String password, Long trainingId) {
        LOG.info("Deleting training with id: {} for trainer username: {}", trainingId, trainerUsername);

        Trainer authenticatedTrainer = authService.authenticateTrainer(trainerUsername, password);
        Training fetchedTraining = findTrainingByIdOrThrow(trainingId);

        if (!authenticatedTrainer.getTrainerId().equals(fetchedTraining.getTrainer().getTrainerId())) {
            throw new InvalidRequestException("Authenticated trainer cannot delete this training");
        }

        trainingRepository.delete(fetchedTraining);
    }

    @Transactional(readOnly = true)
    public TrainingResponse getTrainingById(String trainerUsername, String password, Long trainingId) {
        LOG.info("Getting training by id: {} for trainer username: {}", trainingId, trainerUsername);

        Trainer authenticatedTrainer = authService.authenticateTrainer(trainerUsername, password);
        Training fetchedTraining = findTrainingByIdOrThrow(trainingId);

        if (!authenticatedTrainer.getTrainerId().equals(fetchedTraining.getTrainer().getTrainerId())) {
            throw new InvalidRequestException("Authenticated trainer cannot access this training");
        }

        return TrainingMapper.toResponse(fetchedTraining);
    }

    @Transactional(readOnly = true)
    public List<TrainingResponse> getTrainingsByTraineeUsernameAndCriteria(
            String username,
            String password,
            LocalDate from,
            LocalDate to,
            String trainerName,
            String trainingTypeName
    ) {
        LOG.info(
                "Getting trainings for trainee username: {} with criteria fromDate={}, toDate={}, trainerName={}, trainingType={}",
                username,
                from,
                to,
                trainerName,
                trainingTypeName
        );

        authService.authenticateTrainee(username, password);

        return trainingRepository.findByTraineeUsernameAndCriteria(
                        username,
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
            String username,
            String password,
            LocalDate from,
            LocalDate to,
            String traineeName
    ) {
        LOG.info(
                "Getting trainings for trainer username: {} with criteria fromDate={}, toDate={}, traineeName={}",
                username,
                from,
                to,
                traineeName
        );

        authService.authenticateTrainer(username, password);

        return trainingRepository.findByTrainerUsernameAndCriteria(username, from, to, traineeName)
                .stream()
                .map(TrainingMapper::toResponse)
                .toList();
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
