package com.elioth.epam.gymcrm.service;

import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.TrainingType;
import com.elioth.epam.gymcrm.domain.User;
import com.elioth.epam.gymcrm.dto.mapper.TrainerMapper;
import com.elioth.epam.gymcrm.dto.request.ChangePasswordRequest;
import com.elioth.epam.gymcrm.dto.request.CreateTrainerRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTrainerRequest;
import com.elioth.epam.gymcrm.dto.response.CreatedTrainerResponse;
import com.elioth.epam.gymcrm.dto.response.TrainerResponse;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.exception.IncorrectPasswordException;
import com.elioth.epam.gymcrm.exception.InvalidRequestException;
import com.elioth.epam.gymcrm.repository.TrainerRepository;
import com.elioth.epam.gymcrm.repository.TrainingTypeRepository;
import com.elioth.epam.gymcrm.repository.UserRepository;
import com.elioth.epam.gymcrm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainerService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainerService.class);

    private TrainerRepository trainerRepository;
    private UserRepository userRepository;
    private TrainingTypeRepository trainingTypeRepository;

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Autowired
    public  void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setTrainingTypeRepository(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Transactional
    public CreatedTrainerResponse createProfile(CreateTrainerRequest request) {
        LOG.info("Creating trainer profile");

        validateCreationRequest(request);

        long userCount = userRepository.countByFirstNameAndLastName(request.firstName(), request.lastName());

        TrainingType trainingType = findTrainingTypeOrThrow(request.trainingTypeId());
        String username = Utils.usernameGenerator(request.firstName(), request.lastName(), userCount);
        String password = Utils.generateRandomPassword();


        User user = new User();

        user.setUsername(username);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        Trainer trainer = new Trainer();
        trainer.setUser(savedUser);
        trainer.setSpecialization(trainingType);

        Trainer savedTrainer = trainerRepository.save(trainer);

        LOG.info("Created trainer profile with id: {} referencing user with id: {}", savedTrainer.getTrainerId(), savedUser.getUserId());

        return TrainerMapper.toCreatedResponse(savedTrainer);
    }

    @Transactional
    public TrainerResponse updateProfile(long id, UpdateTrainerRequest request) {
        LOG.info("Updating trainer profile with id: {}", id);

        validateUpdateRequest(request);

        Trainer fetchedTrainer = findTrainerOrThrow(id);
        TrainingType trainingType = findTrainingTypeOrThrow(request.trainingTypeId());

        fetchedTrainer.getUser().setFirstName(request.firstName());
        fetchedTrainer.getUser().setLastName(request.lastName());
        fetchedTrainer.setSpecialization(trainingType);

        LOG.info("Updated trainer profile with id: {}", fetchedTrainer.getTrainerId());

        return TrainerMapper.toResponse(fetchedTrainer);
    }

    public void deleteProfileById(long id) {
        LOG.info("Deleting trainer profile with id: {}", id);

        trainerRepository.deleteById(id);

        LOG.info("Deleted trainer profile with id: {}", id);
    }

    public void deleteProfileByUsername(String username) {
        if (username == null || username.isBlank())
            throw new InvalidRequestException("Invalid username");

        LOG.info("Deleting trainer profile by username: {}", username);

        trainerRepository.deleteByUserUsername(username);

    }

    public TrainerResponse getProfileById(long id) {
        LOG.info("Getting trainer profile by id: {}", id);

        Trainer fetchedTrainer = findTrainerOrThrow(id);

        return TrainerMapper.toResponse(fetchedTrainer);
    }

    public TrainerResponse getProfileByUsername(String username) {
        if ( username == null || username.isBlank())
            throw new InvalidRequestException("Invalid username");

        LOG.info("Getting trainer profile by username: {}", username);

        Trainer fetchedTrainer = findTrainerByUsernameOrThrow(username);

        return TrainerMapper.toResponse(fetchedTrainer);
    }

    @Transactional
    public TrainerResponse changePassword(long id, ChangePasswordRequest request) {
        LOG.info("Changing trainer password with id: {}", id);

        validateChangePasswordRequest(request);

        Trainer fetchedTrainer = findTrainerOrThrow(id);

        checkOldPassword(fetchedTrainer.getUser(), request.oldPassword());

        fetchedTrainer.getUser().setPassword(request.newPassword());

        return TrainerMapper.toResponse(fetchedTrainer);
    }

    public List<TrainerResponse> findBySpecializationName(String name) {
        LOG.info("Getting trainers by specialization: {}", name);

        return trainerRepository.findAllBySpecializationName(name.toUpperCase()).stream()
                .map(TrainerMapper::toResponse)
                .toList();
    }

    public List<TrainerResponse> getTrainersNotAssignedToTraineeByUsername(String username){
        LOG.info("Getting all trainers not assigned to trainee with username: {}", username);

        return trainerRepository.findTrainersNotAssignedToTrainee(username).stream()
                .map(TrainerMapper::toResponse)
                .toList();
    }

    @Transactional
    public void activate(long id) {
        LOG.info("Activating trainer profile with id: {}", id);

        Trainer fetchedTrainer = findTrainerOrThrow(id);

        if (fetchedTrainer.getUser().isActive())
            throw new InvalidRequestException("Trainer is already active.");
        else
            fetchedTrainer.getUser().setActive(true);
    }

    @Transactional
    public void deactivate(long id) {
        LOG.info("Deactivating trainer profile with id: {}", id);

        Trainer fetchedTrainer = findTrainerOrThrow(id);

        if (!fetchedTrainer.getUser().isActive())
            throw new InvalidRequestException("Trainer is already deactivated.");
        else
            fetchedTrainer.getUser().setActive(false);
    }

    @Transactional(readOnly = true)
    public TrainerResponse authenticate(String username, String password) {
        LOG.info("Authenticating trainer with username: {} and password: {}", username, password);

        Trainer fetchedTrainer = findTrainerByUsernameAndPasswordOrThrow(username, password);

        return TrainerMapper.toResponse(fetchedTrainer);
    }

    // ##################### utils ##############################3

    public void validateChangePasswordRequest(ChangePasswordRequest request) {
        if (request == null)
            throw new InvalidRequestException("Invalid request");

        if (request.oldPassword() == null || request.oldPassword().isBlank())
            throw new InvalidRequestException("Invalid old password");
        if (request.newPassword() == null || request.newPassword().isBlank())
            throw new InvalidRequestException("Invalid new password");
    }

    public void validateCreationRequest(CreateTrainerRequest request) {
        if (request == null)
            throw new InvalidRequestException("Request cannot be null");

        if (request.firstName() == null || request.firstName().isBlank())
            throw new InvalidRequestException("First name cannot be empty");
        if (request.lastName() == null || request.lastName().isBlank())
            throw new InvalidRequestException("Last name cannot be empty");
        if (request.trainingTypeId() == null)
            throw  new InvalidRequestException("Training type id cannot be null");
    }

    public void validateUpdateRequest(UpdateTrainerRequest request) {
        if (request == null)
            throw new InvalidRequestException("Request cannot be null");

        if (request.firstName() == null || request.firstName().isBlank())
            throw new InvalidRequestException("First name cannot be empty");
        if (request.lastName() == null || request.lastName().isBlank())
            throw new InvalidRequestException("Last name cannot be empty");
        if (request.trainingTypeId() == null)
            throw new InvalidRequestException("Training type id cannot be null");
    }

    public void checkOldPassword(User user, String oldPassword) {
        if (oldPassword == null || oldPassword.isBlank())
            throw new InvalidRequestException("Invalid old password");
        if (!oldPassword.equals(user.getPassword()))
            throw new IncorrectPasswordException("Incorrect old password");
    }

    public Trainer findTrainerOrThrow(long trainerId) {
        return trainerRepository.findById(trainerId)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found"));
    }

    public TrainingType findTrainingTypeOrThrow(long trainingTypeId) {
        return trainingTypeRepository.findById(trainingTypeId)
                .orElseThrow(() -> new EntityNotFoundException("TrainingType not found"));
    }

    public Trainer findTrainerByUsernameOrThrow(String username) {
        return trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found"));
    }

    public Trainer findTrainerByUsernameAndPasswordOrThrow(String username, String password) {
        return trainerRepository.findByUserUsernameAndUserPassword(username, password)
                .orElseThrow(() -> new EntityNotFoundException("Username or password incorrect"));
    }
}