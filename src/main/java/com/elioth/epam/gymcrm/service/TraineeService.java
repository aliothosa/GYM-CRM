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

@Service
public class TraineeService {

    private static final Logger LOG = LoggerFactory.getLogger(TraineeService.class);

    private TraineeRepository traineeRepository;
    private TrainerRepository trainerRepository;
    private UserRepository userRepository;

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

    @Transactional
    public CreatedTraineeResponse createProfile(CreateTraineeRequest request) {
        LOG.info("Creating trainee profile");

        validateCreationRequest(request);

        long userCount = userRepository.countByFirstNameAndLastName(request.firstName(),  request.lastName());

        String username = Utils.usernameGenerator(request.firstName(), request.lastName(), userCount);
        String password = Utils.generateRandomPassword();

        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        Trainee trainee = new Trainee();
        trainee.setUser(savedUser);
        trainee.setAddress(request.address());
        trainee.setBirthDate(request.birthDate());

        Trainee savedTrainee = traineeRepository.save(trainee);

        LOG.info("Created trainee profile with id: {} referencing user with id: {}", savedTrainee.getTraineeId(), savedUser.getUserId());

        return TraineeMapper.toCreatedResponse(savedTrainee);
    }

    @Transactional
    public TraineeResponse updateProfile(long id, UpdateTraineeRequest request) {
        LOG.info("Updating trainee profile with id: {}", id);

        validateUpdateRequest(request);

        Trainee fetchedTrainee = findTraineeByIdOrThrow(id);

        fetchedTrainee.getUser().setFirstName(request.firstName());
        fetchedTrainee.getUser().setLastName(request.lastName());
        fetchedTrainee.setBirthDate(request.birthDate());
        fetchedTrainee.setAddress(request.address());

        return TraineeMapper.toResponse(fetchedTrainee);
    }

    public void deleteProfileById(long id) {
        LOG.info("Deleting trainee profile with id: {}", id);

        traineeRepository.deleteById(id);

    }

    public void deleteProfileByUsername(String username) {
        LOG.info("Deleting trainee profile by username: {}", username);

        traineeRepository.deleteByUserUsername(username);

    }

    @Transactional(readOnly = true)
    public TraineeResponse getProfileById(long id) {
        LOG.info("Getting trainee profile by id: {}", id);

        Trainee  fetchedTrainee = findTraineeByIdOrThrow(id);

        return TraineeMapper.toResponse(fetchedTrainee);
    }

    @Transactional(readOnly = true)
    public TraineeResponse getProfileByUsername(String username){
        if (username == null || username.isBlank())
            throw new InvalidRequestException("Invalid username");

        LOG.info("Getting trainee profile by username: {}", username);

        Trainee fetchedTrainee = findTraineeByUsernameOrThrow(username);

        return TraineeMapper.toResponse(fetchedTrainee);
    }

    @Transactional
    public TraineeResponse changePassword(long id, ChangePasswordRequest request) {
        LOG.info("Changing trainee password with id: {}", id);

        validateChangePasswordRequest(request);

        Trainee fetchedTrainee = findTraineeByIdOrThrow(id);

        checkOldPassword(fetchedTrainee.getUser(), request.oldPassword());

        fetchedTrainee.getUser().setPassword(request.newPassword());

        return TraineeMapper.toResponse(fetchedTrainee);
    }

    @Transactional
    public void updateTrainersToTrainee(long traineeId, List<Long> trainerIds) {
        if (trainerIds == null || trainerIds.isEmpty())
            throw new InvalidRequestException("Invalid list of Trainees");

        LOG.info("Adding trainers with ids: {} to trainee with id: {}", trainerIds, traineeId);

        Trainee fetchedTrainee = findTraineeByIdOrThrow(traineeId);

        List<Trainer> fetchedTrainers = trainerRepository.findAllById(trainerIds);

        fetchedTrainee.getTrainers().clear();
        fetchedTrainee.getTrainers().addAll(fetchedTrainers);
    }

    @Transactional
    public void activate(long id) {
        LOG.info("Activating trainee profile with id: {}", id);

        Trainee fetchedTrainee = findTraineeByIdOrThrow(id);

        if (fetchedTrainee.getUser().isActive())
            throw new InvalidRequestException("Trainee is already active.");
        else
            fetchedTrainee.getUser().setActive(true);
    }

    @Transactional
    public void deactivate(long id) {
        LOG.info("Deactivating trainee profile with id: {}", id);

        Trainee fetchedTrainee = findTraineeByIdOrThrow(id);

        if (!fetchedTrainee.getUser().isActive())
            throw new InvalidRequestException("Trainee is already deactivated.");
        else
            fetchedTrainee.getUser().setActive(false);
    }

    @Transactional(readOnly = true)
    public TraineeResponse authenticate(String username, String password) {
        LOG.info("Authenticating trainee with username: {} and password: {}", username, password);

        Trainee fetchedTrainee = findTraineeByUsernameAndPasswordOrThrow(username, password);

        return TraineeMapper.toResponse(fetchedTrainee);
    }

    // ########################## Utils ##########################

    public void validateChangePasswordRequest(ChangePasswordRequest request) {
        if (request == null)
            throw new InvalidRequestException("Invalid request");

        if (request.oldPassword() == null || request.oldPassword().isBlank())
            throw new InvalidRequestException("Invalid old password");
        if (request.newPassword() == null || request.newPassword().isBlank())
            throw new InvalidRequestException("Invalid new password");
    }

    public void validateCreationRequest(CreateTraineeRequest request) {
        if (request == null)
            throw new InvalidRequestException("Request cannot be null");

        if (request.firstName() == null || request.firstName().isBlank())
            throw new InvalidRequestException("First name cannot be empty");
        if (request.lastName() == null || request.lastName().isBlank())
            throw new InvalidRequestException("Last name cannot be empty");
        if (request.address() == null)
            throw new InvalidRequestException("Address cannot be null");
        if (request.birthDate() == null)
            throw new InvalidRequestException("Birth date cannot be empty");
    }

    public void validateUpdateRequest(UpdateTraineeRequest request) {
        if (request == null)
            throw new InvalidRequestException("Request cannot be null");

        if (request.firstName() == null || request.firstName().isBlank())
            throw new InvalidRequestException("First name cannot be empty");
        if (request.lastName() == null || request.lastName().isBlank())
            throw new InvalidRequestException("Last name cannot be empty");
        if (request.address() == null)
            throw new InvalidRequestException("Address cannot be null");
        if (request.birthDate() == null)
            throw new InvalidRequestException("Birth date cannot be empty");
    }

    public void checkOldPassword(User user, String oldPassword) {
        if (oldPassword == null || oldPassword.isBlank())
            throw new InvalidRequestException("Invalid old password");
        if (!oldPassword.equals(user.getPassword()))
            throw new IncorrectPasswordException("Incorrect old password");
    }

    public Trainee findTraineeByIdOrThrow(long traineeId) {
        return traineeRepository.findById(traineeId)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found"));
    }

    public Trainee findTraineeByUsernameOrThrow(String username) {
        return traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found"));
    }

    public Trainee findTraineeByUsernameAndPasswordOrThrow(String username, String password) {
        return traineeRepository.findByUserUsernameAndUserPassword(username, password)
                .orElseThrow(() -> new EntityNotFoundException("Username or password incorrect"));
    }
}