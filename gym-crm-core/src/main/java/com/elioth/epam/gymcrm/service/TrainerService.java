package com.elioth.epam.gymcrm.service;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.TrainingType;
import com.elioth.epam.gymcrm.domain.User;
import com.elioth.epam.gymcrm.dto.mapper.TrainerMapper;
import com.elioth.epam.gymcrm.dto.request.ChangePasswordRequest;
import com.elioth.epam.gymcrm.dto.request.CreateTrainerRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTrainerRequest;
import com.elioth.epam.gymcrm.dto.response.CreatedTrainerResponse;
import com.elioth.epam.gymcrm.dto.response.EmbeddedTrainerResponse;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class TrainerService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainerService.class);

    private TrainerRepository trainerRepository;
    private UserRepository userRepository;
    private TrainingTypeRepository trainingTypeRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setTrainingTypeRepository(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CreatedTrainerResponse createProfile(CreateTrainerRequest request) {
        LOG.info("Creating trainer profile");

        validateCreationRequest(request);

        long userCount = userRepository.countByFirstNameAndLastName(request.firstName(), request.lastName());
        TrainingType trainingType = findTrainingTypeOrThrow(request.trainingTypeId());
        String username = Utils.usernameGenerator(request.firstName(), request.lastName(), userCount);
        String rawPassword = Utils.generateRandomPassword();

        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(trainingType);

        Trainer savedTrainer = trainerRepository.save(trainer);

        LOG.info("Created trainer profile with id: {} and username: {}", savedTrainer.getTrainerId(), username);

        return TrainerMapper.toCreatedResponse(savedTrainer, rawPassword);
    }

    @Transactional
    public TrainerResponse updateProfile(Long trainerId, UpdateTrainerRequest request) {
        LOG.info("Updating trainer profile with id: {}", trainerId);

        validateUpdateRequest(request);

        Trainer fetchedTrainer = findTrainerByIdOrThrow(trainerId);

        TrainingType trainingType = findTrainingTypeByNameOrThrow(request.trainingType());

        fetchedTrainer.getUser().setFirstName(request.firstName());
        fetchedTrainer.getUser().setLastName(request.lastName());
        fetchedTrainer.setSpecialization(trainingType);

        return TrainerMapper.toResponse(fetchedTrainer);
    }

    @Transactional
    public TrainerResponse updateProfile(String username, UpdateTrainerRequest request) {
        LOG.info("Updating trainer profile with username: {}", username);

        validateUpdateRequest(request);

        Trainer fetchedTrainer = findTrainerByUsernameOrThrow(username);

        TrainingType trainingType = findTrainingTypeByNameOrThrow(request.trainingType());

        fetchedTrainer.getUser().setFirstName(request.firstName());
        fetchedTrainer.getUser().setLastName(request.lastName());
        fetchedTrainer.setSpecialization(trainingType);

        return TrainerMapper.toResponse(fetchedTrainer);
    }

    @Transactional(readOnly = true)
    public TrainerResponse getProfileById(Long trainerId) {
        LOG.info("Getting trainer profile by id: {}", trainerId);

        return TrainerMapper.toResponse(findTrainerByIdOrThrow(trainerId));
    }

    @Transactional(readOnly = true)
    public TrainerResponse getProfileByUsername(String username) {
        LOG.info("Getting trainer profile by username: {}", username);

        return TrainerMapper.toResponse(findTrainerByUsernameOrThrow(username));
    }

    @Transactional
    public TrainerResponse changePassword(Long trainerId, ChangePasswordRequest request) {
        LOG.info("Changing trainer password for id: {}", trainerId);

        validateChangePasswordRequest(request);

        Trainer fetchedTrainer = findTrainerByIdOrThrow(trainerId);
        checkOldPassword(fetchedTrainer.getUser(), request.oldPassword());
        fetchedTrainer.getUser().setPassword(passwordEncoder.encode(request.newPassword()));

        return TrainerMapper.toResponse(fetchedTrainer);
    }

    @Transactional(readOnly = true)
    public List<TrainerResponse> findBySpecializationName(String name) {
        LOG.info("Getting trainers by specialization: {}", name);

        if (name == null || name.isBlank()) {
            throw new InvalidRequestException("Specialization name cannot be empty");
        }

        return trainerRepository.findAllBySpecializationName(name.toUpperCase()).stream()
                .map(TrainerMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TrainerResponse> getTrainersNotAssignedToTrainee(String traineeUsername) {
        LOG.info("Getting all trainers not assigned to trainee with username: {}", traineeUsername);

        if (traineeUsername == null || traineeUsername.isBlank()) {
            throw new InvalidRequestException("Invalid trainee username");
        }

        return trainerRepository.findTrainersNotAssignedToTrainee(traineeUsername).stream()
                .map(TrainerMapper::toResponse)
                .toList();
    }

    @Transactional
    public List<EmbeddedTrainerResponse> getTrainersNotAssignedToTraineeEmbedded(String traineeUsername) {
        LOG.info("Getting all trainers not assigned to trainee with username: {}", traineeUsername);

        if (traineeUsername == null || traineeUsername.isBlank()) {
            throw new InvalidRequestException("Invalid trainee username");
        }

        return trainerRepository.findTrainersNotAssignedToTrainee(traineeUsername).stream()
                .map(TrainerMapper::toEmbeddedResponse)
                .toList();
    }

    @Transactional
    public void activate(Long trainerId) {
        LOG.info("Activating trainer profile with id: {}", trainerId);

        Trainer fetchedTrainer = findTrainerByIdOrThrow(trainerId);

        if (Boolean.TRUE.equals(fetchedTrainer.getUser().getActive())) {
            throw new InvalidRequestException("Trainer is already active.");
        }

        fetchedTrainer.getUser().setActive(true);
    }

    @Transactional
    public void deactivate(Long trainerId) {
        LOG.info("Deactivating trainer profile with id: {}", trainerId);

        Trainer fetchedTrainer = findTrainerByIdOrThrow(trainerId);

        if (!Boolean.TRUE.equals(fetchedTrainer.getUser().getActive())) {
            throw new InvalidRequestException("Trainer is already deactivated.");
        }

        fetchedTrainer.getUser().setActive(false);
    }

    @Transactional
    public void setStatus(String username, Boolean activeStatus){
        if (activeStatus == null) {
            throw new InvalidRequestException("active status cannot be null");
        }

        LOG.info("Setting trainee with username: {} active status to '{}'",username, activeStatus ? "active"  : "inactive");

        Trainer fetchedTrainer = findTrainerByUsernameOrThrow(username);

        if (activeStatus.equals(fetchedTrainer.getUser().getActive())) {
            throw new InvalidRequestException(String.format("Trainee is already %s.", activeStatus ? "active"  : "inactive") );
        }
        fetchedTrainer.getUser().setActive(activeStatus);
    }

    private Trainer findTrainerByIdOrThrow(Long trainerId) {
        return trainerRepository.findById(trainerId)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found"));
    }

    private Trainer findTrainerByUsernameOrThrow(String username) {
        if (username == null || username.isBlank()) {
            throw new InvalidRequestException("Invalid username");
        }
        return trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found"));
    }

    private void validateChangePasswordRequest(ChangePasswordRequest request) {
        if (request == null) {
            throw new InvalidRequestException("Invalid request");
        }
        if (request.oldPassword() == null || request.oldPassword().isBlank()) {
            throw new InvalidRequestException("Invalid old password");
        }
        if (request.newPassword() == null || request.newPassword().isBlank()) {
            throw new InvalidRequestException("Invalid new password");
        }
    }

    private void validateCreationRequest(CreateTrainerRequest request) {
        if (request == null) {
            throw new InvalidRequestException("Request cannot be null");
        }
        if (request.firstName() == null || request.firstName().isBlank()) {
            throw new InvalidRequestException("First name cannot be empty");
        }
        if (request.lastName() == null || request.lastName().isBlank()) {
            throw new InvalidRequestException("Last name cannot be empty");
        }
        if (request.trainingTypeId() == null) {
            throw new InvalidRequestException("Training type id cannot be null");
        }
    }

    private void validateUpdateRequest(UpdateTrainerRequest request) {
        if (request == null) {
            throw new InvalidRequestException("Request cannot be null");
        }

        if (request.username() == null || request.username().isBlank()) {
            throw new InvalidRequestException("Username cannot be empty");
        }
        if (request.firstName() == null || request.firstName().isBlank()) {
            throw new InvalidRequestException("First name cannot be empty");
        }
        if (request.lastName() == null || request.lastName().isBlank()) {
            throw new InvalidRequestException("Last name cannot be empty");
        }
        if (request.isActive() == null ) {
            throw new InvalidRequestException("Is active cannot be null");
        }


    }

    private void checkOldPassword(User user, String oldPassword) {
        if (oldPassword == null || oldPassword.isBlank()) {
            throw new InvalidRequestException("Invalid old password");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IncorrectPasswordException("Incorrect old password");
        }
    }

    private TrainingType findTrainingTypeOrThrow(Long trainingTypeId) {
        return trainingTypeRepository.findById(trainingTypeId)
                .orElseThrow(() -> new EntityNotFoundException("TrainingType not found"));
    }

    private TrainingType findTrainingTypeByNameOrThrow(String name) {
        return trainingTypeRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("TrainingType not found"));
    }
}
