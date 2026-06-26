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
    public TrainingResponse createTraining(CreateTrainingRequest request) {
        LOG.info("Creating training");

        validateCreateRequest(request);

        Trainee fetchedTrainee = findTraineeByIdOrThrow(request.traineeId());
        Trainer fetchedTrainer = findTrainerByIdOrThrow(request.trainerId());
        TrainingType fetchedTrainingType = findTrainingTypeByIdOrThrow(request.trainingTypeId());

        Training training = new Training();

        training.setTrainee(fetchedTrainee);
        training.setTrainer(fetchedTrainer);
        training.setType(fetchedTrainingType);

        training.setName(request.trainingName());
        training.setDate(request.trainingDate());
        training.setDurationInMinutes(request.duration());

        Training savedTraining = trainingRepository.save(training);


        LOG.info("Created training with id: {}", savedTraining.getTrainingId());

        return TrainingMapper.toResponse(savedTraining);
    }

    @Transactional
    public TrainingResponse updateTraining(long id, UpdateTrainingRequest request) {
        LOG.info("Updating training with id: {}", id);

        validateUpdateRequest(request);

        Training fetchedTraining = findTrainingByIdOrThrow(id);

        fetchedTraining.setName(request.name());
        fetchedTraining.setDate(request.date());
        fetchedTraining.setDurationInMinutes(request.duration());

        LOG.info("Updated training with id: {}", fetchedTraining.getTrainingId());

        return TrainingMapper.toResponse(fetchedTraining);
    }

    public void deleteTraining(long trainingId) {
        LOG.info("Deleting training with id: {}", trainingId);

        trainingRepository.deleteById(trainingId);

    }

    public TrainingResponse getTrainingById(long trainingId) {
        LOG.info("Getting training by id: {}", trainingId);

        Training fetchedTraining = findTrainingByIdOrThrow(trainingId);

        return TrainingMapper.toResponse(fetchedTraining);
    }

    public List<TrainingResponse> getTrainingsByTraineeId(long traineeId) {
        LOG.info("Getting trainings by trainee id: {}", traineeId);

        return trainingRepository.findAllByTraineeTraineeId(traineeId).stream()
                .map(TrainingMapper::toResponse)
                .toList();
    }

    public List<TrainingResponse> getTrainingsByTrainerId(long trainerId) {
        LOG.info("Getting trainings by trainer id: {}", trainerId);

        return trainingRepository.findAllByTrainerTrainerId(trainerId).stream()
                .map(TrainingMapper::toResponse)
                .toList();
    }

    public List<TrainingResponse> getTrainingsByTypeName(String trainingTypeName) {
        LOG.info("Getting trainings by type: {}", trainingTypeName);

        return trainingRepository.findAllByTypeName(trainingTypeName.toUpperCase())
                .stream()
                .map(TrainingMapper::toResponse)
                .toList();
    }

    public List<TrainingResponse> getTrainingsByDateBetween(LocalDate from, LocalDate to) {
        LOG.info("Getting trainings between dates: {} and {}", from, to);

        return trainingRepository.findAllByDateBetween(from, to).stream()
                .map(TrainingMapper::toResponse)
                .toList();
    }

    public List<TrainingResponse> GetTrainingsByTraineeUsernameAndCriteria(String username, LocalDate from, LocalDate to) {
        LOG.info("Getting all trainings for trainee with username: {} and dates between {} and {}",  username, from, to);

        return trainingRepository.findAllByDateBetweenAndTraineeUserUsername(from, to, username)
                .stream()
                .map(TrainingMapper::toResponse)
                .toList();
    }

    public List<TrainingResponse> GetTrainingsByTrainerUsernameAndCriteria(String username, LocalDate from, LocalDate to) {
        LOG.info("Getting all trainings for trainer with username: {} and dates between {} and {}",  username, from, to);

        return trainingRepository.findAllByDateBetweenAndTrainerUserUsername(from, to, username)
                .stream()
                .map(TrainingMapper::toResponse)
                .toList();
    }

    // #################### utils ##########################

    public void validateCreateRequest(CreateTrainingRequest request) {
        if (request == null)
            throw new InvalidEntityException("Request cannot be null");

        if (request.trainingName() == null || request.trainingName().isBlank())
            throw new InvalidEntityException("Training name cannot be empty");
        if (request.trainingDate() == null)
            throw new InvalidEntityException("Training date cannot be null");
        if (request.duration() <= 0)
            throw new InvalidEntityException("Training duration must be greater than 0");
    }

    public void validateUpdateRequest(UpdateTrainingRequest request) {
        if (request == null)
            throw new InvalidEntityException("Request cannot be null");

        if (request.name() == null || request.name().isBlank())
            throw new InvalidEntityException("Training name cannot be empty");
        if (request.duration() <= 0)
            throw new InvalidEntityException("Training duration must be greater than 0");
        if (request.date() == null)
            throw new InvalidEntityException("Training date cannot be null");
    }

    public void validateTrainingTypeName(String trainingTypeName) {
        if (trainingTypeName == null || trainingTypeName.isBlank())
            throw new InvalidEntityException("Training type name cannot be empty");
    }

    public Training findTrainingByIdOrThrow(long id) {
        return trainingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Training with id " + id + " not found"));
    }

    public Trainee findTraineeByIdOrThrow(long id) {
        return traineeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainee with id " + id + " not found"));
    }

    public Trainer findTrainerByIdOrThrow(long id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainer with id " + id + " not found"));
    }

    public TrainingType findTrainingTypeByIdOrThrow(long id) {
        return trainingTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Training type with id " + id + " not found"));
    }

}