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
import java.util.Objects;

@Service
public class TrainerService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainerService.class);

    private TrainerRepository trainerRepository;
    private UserRepository userRepository;
    private TrainingTypeRepository trainingTypeRepository;
    private AuthService authService;

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
    public void setAuthService(AuthService authService) {
        this.authService = authService;
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
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(trainingType);

        Trainer savedTrainer = trainerRepository.save(trainer);

        LOG.info("Created trainer profile with id: {} and username: {}", savedTrainer.getTrainerId(), username);

        return TrainerMapper.toCreatedResponse(savedTrainer);
    }

    @Transactional
    public TrainerResponse updateProfile(String username, String password, UpdateTrainerRequest request) {
        LOG.info("Updating trainer profile for username: {}", username);

        validateUpdateRequest(request);

        Trainer fetchedTrainer = authService.authenticateTrainer(username, password);
        TrainingType trainingType = findTrainingTypeOrThrow(request.trainingTypeId());

        fetchedTrainer.getUser().setFirstName(request.firstName());
        fetchedTrainer.getUser().setLastName(request.lastName());
        fetchedTrainer.setSpecialization(trainingType);

        return TrainerMapper.toResponse(fetchedTrainer);
    }

    @Transactional(readOnly = true)
    public TrainerResponse getProfileByUsername(String username, String password) {
        LOG.info("Getting trainer profile by username: {}", username);

        Trainer fetchedTrainer = authService.authenticateTrainer(username, password);
        return TrainerMapper.toResponse(fetchedTrainer);
    }

    @Transactional
    public TrainerResponse changePassword(String username, ChangePasswordRequest request) {
        LOG.info("Changing trainer password for username: {}", username);

        validateChangePasswordRequest(request);

        Trainer fetchedTrainer = authService.authenticateTrainer(username, request.oldPassword());
        checkOldPassword(fetchedTrainer.getUser(), request.oldPassword());
        fetchedTrainer.getUser().setPassword(request.newPassword());

        return TrainerMapper.toResponse(fetchedTrainer);
    }

    @Transactional(readOnly = true)
    public List<TrainerResponse> findBySpecializationName(String username, String password, String name) {
        LOG.info("Getting trainers by specialization: {}", name);

        authService.authenticateTrainer(username, password);

        if (name == null || name.isBlank()) {
            throw new InvalidRequestException("Specialization name cannot be empty");
        }

        return trainerRepository.findAllBySpecializationName(name.toUpperCase()).stream()
                .map(TrainerMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TrainerResponse> getTrainersNotAssignedToTrainee(String traineeUsername, String password) {
        LOG.info("Getting all trainers not assigned to trainee with username: {}", traineeUsername);

        authService.authenticateTrainee(traineeUsername, password);

        return trainerRepository.findTrainersNotAssignedToTrainee(traineeUsername).stream()
                .map(TrainerMapper::toResponse)
                .toList();
    }

    @Transactional
    public void activate(String username, String password) {
        LOG.info("Activating trainer profile for username: {}", username);

        Trainer fetchedTrainer = authService.authenticateTrainer(username, password);

        if (Boolean.TRUE.equals(fetchedTrainer.getUser().getActive())) {
            throw new InvalidRequestException("Trainer is already active.");
        }

        fetchedTrainer.getUser().setActive(true);
    }

    @Transactional
    public void deactivate(String username, String password) {
        LOG.info("Deactivating trainer profile for username: {}", username);

        Trainer fetchedTrainer = authService.authenticateTrainer(username, password);

        if (!Boolean.TRUE.equals(fetchedTrainer.getUser().getActive())) {
            throw new InvalidRequestException("Trainer is already deactivated.");
        }

        fetchedTrainer.getUser().setActive(false);
    }

    @Transactional(readOnly = true)
    public TrainerResponse authenticate(String username, String password) {
        Trainer fetchedTrainer = authService.authenticateTrainer(username, password);
        return TrainerMapper.toResponse(fetchedTrainer);
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

    private void checkOldPassword(User user, String oldPassword) {
        if (oldPassword == null || oldPassword.isBlank()) {
            throw new InvalidRequestException("Invalid old password");
        }
        if (!Objects.equals(oldPassword, user.getPassword())) {
            throw new IncorrectPasswordException("Incorrect old password");
        }
    }

    private TrainingType findTrainingTypeOrThrow(Long trainingTypeId) {
        return trainingTypeRepository.findById(trainingTypeId)
                .orElseThrow(() -> new EntityNotFoundException("TrainingType not found"));
    }
}
