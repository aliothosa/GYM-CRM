package com.elioth.epam.gymcrm.service;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.User;
import com.elioth.epam.gymcrm.dto.mapper.TraineeMapper;
import com.elioth.epam.gymcrm.dto.request.ChangePasswordRequest;
import com.elioth.epam.gymcrm.dto.request.CreateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTraineeRequest;
import com.elioth.epam.gymcrm.dto.response.CreatedTraineeResponse;
import com.elioth.epam.gymcrm.dto.response.TraineeResponse;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.exception.IncorrectPasswordException;
import com.elioth.epam.gymcrm.exception.InvalidRequestException;
import com.elioth.epam.gymcrm.repository.TraineeRepository;
import com.elioth.epam.gymcrm.repository.TrainerRepository;
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
public class TraineeService {

    private static final Logger LOG = LoggerFactory.getLogger(TraineeService.class);

    private TraineeRepository traineeRepository;
    private TrainerRepository trainerRepository;
    private UserRepository userRepository;
    private AuthService authService;

    @Autowired
    public void setTraineeRepository(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Autowired
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @Transactional
    public CreatedTraineeResponse createProfile(CreateTraineeRequest request) {
        LOG.info("Creating trainee profile");

        validateCreationRequest(request);

        long userCount = userRepository.countByFirstNameAndLastName(request.firstName(), request.lastName());
        String username = Utils.usernameGenerator(request.firstName(), request.lastName(), userCount);
        String password = Utils.generateRandomPassword();

        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setAddress(request.address());
        trainee.setBirthDate(request.birthDate());

        Trainee savedTrainee = traineeRepository.save(trainee);

        LOG.info("Created trainee profile with id: {} and username: {}", savedTrainee.getTraineeId(), username);

        return TraineeMapper.toCreatedResponse(savedTrainee);
    }

    @Transactional
    public TraineeResponse updateProfile(String username, String password, UpdateTraineeRequest request) {
        LOG.info("Updating trainee profile for username: {}", username);

        validateUpdateRequest(request);

        Trainee fetchedTrainee = authService.authenticateTrainee(username, password);

        fetchedTrainee.getUser().setFirstName(request.firstName());
        fetchedTrainee.getUser().setLastName(request.lastName());
        fetchedTrainee.setBirthDate(request.birthDate());
        fetchedTrainee.setAddress(request.address());

        return TraineeMapper.toResponse(fetchedTrainee);
    }

    @Transactional
    public void deleteProfileByUsername(String username, String password) {
        LOG.info("Deleting trainee profile by username: {}", username);

        Trainee fetchedTrainee = authService.authenticateTrainee(username, password);
        traineeRepository.delete(fetchedTrainee);
    }

    @Transactional(readOnly = true)
    public TraineeResponse getProfileByUsername(String username, String password) {
        LOG.info("Getting trainee profile by username: {}", username);

        Trainee fetchedTrainee = authService.authenticateTrainee(username, password);
        return TraineeMapper.toResponse(fetchedTrainee);
    }

    @Transactional
    public TraineeResponse changePassword(String username, ChangePasswordRequest request) {
        LOG.info("Changing trainee password for username: {}", username);

        validateChangePasswordRequest(request);

        Trainee fetchedTrainee = authService.authenticateTrainee(username, request.oldPassword());
        checkOldPassword(fetchedTrainee.getUser(), request.oldPassword());
        fetchedTrainee.getUser().setPassword(request.newPassword());

        return TraineeMapper.toResponse(fetchedTrainee);
    }

    @Transactional
    public void updateTrainersToTrainee(String username, String password, List<Long> trainerIds) {
        if (trainerIds == null) {
            throw new InvalidRequestException("Trainer list cannot be null");
        }

        LOG.info("Updating trainers list for trainee with username: {}", username);

        Trainee fetchedTrainee = authService.authenticateTrainee(username, password);
        List<Trainer> fetchedTrainers = trainerRepository.findAllById(trainerIds);

        if (fetchedTrainers.size() != trainerIds.size()) {
            throw new EntityNotFoundException("One or more trainers were not found");
        }

        fetchedTrainee.getTrainers().clear();
        fetchedTrainee.getTrainers().addAll(fetchedTrainers);
    }

    @Transactional
    public void activate(String username, String password) {
        LOG.info("Activating trainee profile for username: {}", username);

        Trainee fetchedTrainee = authService.authenticateTrainee(username, password);

        if (Boolean.TRUE.equals(fetchedTrainee.getUser().getActive())) {
            throw new InvalidRequestException("Trainee is already active.");
        }

        fetchedTrainee.getUser().setActive(true);
    }

    @Transactional
    public void deactivate(String username, String password) {
        LOG.info("Deactivating trainee profile for username: {}", username);

        Trainee fetchedTrainee = authService.authenticateTrainee(username, password);

        if (!Boolean.TRUE.equals(fetchedTrainee.getUser().getActive())) {
            throw new InvalidRequestException("Trainee is already deactivated.");
        }

        fetchedTrainee.getUser().setActive(false);
    }

    @Transactional(readOnly = true)
    public TraineeResponse authenticate(String username, String password) {
        Trainee fetchedTrainee = authService.authenticateTrainee(username, password);
        return TraineeMapper.toResponse(fetchedTrainee);
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

    private void validateCreationRequest(CreateTraineeRequest request) {
        if (request == null) {
            throw new InvalidRequestException("Request cannot be null");
        }
        if (request.firstName() == null || request.firstName().isBlank()) {
            throw new InvalidRequestException("First name cannot be empty");
        }
        if (request.lastName() == null || request.lastName().isBlank()) {
            throw new InvalidRequestException("Last name cannot be empty");
        }
        if (request.address() == null) {
            throw new InvalidRequestException("Address cannot be null");
        }
        if (request.birthDate() == null) {
            throw new InvalidRequestException("Birth date cannot be empty");
        }
    }

    private void validateUpdateRequest(UpdateTraineeRequest request) {
        if (request == null) {
            throw new InvalidRequestException("Request cannot be null");
        }
        if (request.firstName() == null || request.firstName().isBlank()) {
            throw new InvalidRequestException("First name cannot be empty");
        }
        if (request.lastName() == null || request.lastName().isBlank()) {
            throw new InvalidRequestException("Last name cannot be empty");
        }
        if (request.address() == null) {
            throw new InvalidRequestException("Address cannot be null");
        }
        if (request.birthDate() == null) {
            throw new InvalidRequestException("Birth date cannot be empty");
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
}
