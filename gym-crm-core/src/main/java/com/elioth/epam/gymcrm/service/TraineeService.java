package com.elioth.epam.gymcrm.service;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.User;
import com.elioth.epam.gymcrm.dto.mapper.TraineeMapper;
import com.elioth.epam.gymcrm.dto.mapper.TrainerMapper;
import com.elioth.epam.gymcrm.dto.request.ChangePasswordRequest;
import com.elioth.epam.gymcrm.dto.request.CreateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTraineeRequest;
import com.elioth.epam.gymcrm.dto.response.CreatedTraineeResponse;
import com.elioth.epam.gymcrm.dto.response.EmbeddedTrainerResponse;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TraineeService {

    private static final Logger LOG = LoggerFactory.getLogger(TraineeService.class);

    private TraineeRepository traineeRepository;
    private TrainerRepository trainerRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

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
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CreatedTraineeResponse createProfile(CreateTraineeRequest request) {
        LOG.info("Creating trainee profile");

        validateCreationRequest(request);

        long userCount = userRepository.countByFirstNameAndLastName(request.firstName(), request.lastName());
        String username = Utils.usernameGenerator(request.firstName(), request.lastName(), userCount);
        String rawPassword = Utils.generateRandomPassword();

        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setAddress(request.address());
        trainee.setBirthDate(request.birthDate());

        Trainee savedTrainee = traineeRepository.save(trainee);

        LOG.info("Created trainee profile with id: {} and username: {}", savedTrainee.getTraineeId(), username);

        return TraineeMapper.toCreatedResponse(savedTrainee, rawPassword);
    }

    @Transactional
    public TraineeResponse updateProfile(Long traineeId, UpdateTraineeRequest request) {
        LOG.info("Updating trainee profile with id: {}", traineeId);

        validateUpdateRequest(request);

        Trainee fetchedTrainee = findTraineeByIdOrThrow(traineeId);

        User user = fetchedTrainee.getUser();

        user.setUsername(request.username());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        if (Objects.nonNull(request.birthDate())) {
            fetchedTrainee.setBirthDate(request.birthDate());
        }
        if  (Objects.nonNull(request.address())) {
            fetchedTrainee.setAddress(request.address());
        }

        return TraineeMapper.toResponse(fetchedTrainee);
    }

    @Transactional
    public TraineeResponse updateProfile(String username, UpdateTraineeRequest request) {
        LOG.info("Updating trainee profile with username: {}", username);

        validateUpdateRequest(request);
        Trainee fetchedTrainee = findTraineeByUsernameOrThrow(username);

        User user = fetchedTrainee.getUser();

        user.setUsername(request.username());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        if (Objects.nonNull(request.birthDate())) {
            fetchedTrainee.setBirthDate(request.birthDate());
        }
        if  (Objects.nonNull(request.address())) {
            fetchedTrainee.setAddress(request.address());
        }

        return TraineeMapper.toResponse(fetchedTrainee);
    }

    @Transactional
    public void deleteProfile(Long traineeId) {
        LOG.info("Deleting trainee profile with id: {}", traineeId);

        Trainee fetchedTrainee = findTraineeByIdOrThrow(traineeId);
        traineeRepository.delete(fetchedTrainee);
    }

    @Transactional
    public void deleteProfile(String username) {
        LOG.info("Deleting trainee profile with username: {}", username);

        Trainee fetchedTrainee = findTraineeByUsernameOrThrow(username);
        traineeRepository.delete(fetchedTrainee);
    }

    @Transactional(readOnly = true)
    public TraineeResponse getProfileById(Long traineeId) {
        LOG.info("Getting trainee profile by id: {}", traineeId);

        return TraineeMapper.toResponse(findTraineeByIdOrThrow(traineeId));
    }

    @Transactional(readOnly = true)
    public TraineeResponse getProfileByUsername(String username) {
        LOG.info("Getting trainee profile by username: {}", username);

        return TraineeMapper.toResponse(findTraineeByUsernameOrThrow(username));
    }

    @Transactional
    public TraineeResponse changePassword(Long traineeId, ChangePasswordRequest request) {
        LOG.info("Changing trainee password for id: {}", traineeId);

        validateChangePasswordRequest(request);

        Trainee fetchedTrainee = findTraineeByIdOrThrow(traineeId);
        checkOldPassword(fetchedTrainee.getUser(), request.oldPassword());
        fetchedTrainee.getUser().setPassword(passwordEncoder.encode(request.newPassword()));

        return TraineeMapper.toResponse(fetchedTrainee);
    }

    @Transactional
    public void updateTrainersToTrainee(Long traineeId, List<Long> trainerIds) {
        if (trainerIds == null) {
            throw new InvalidRequestException("Trainer list cannot be null");
        }

        LOG.info("Updating trainers list for trainee with id: {}", traineeId);

        Trainee fetchedTrainee = findTraineeByIdOrThrow(traineeId);
        List<Trainer> fetchedTrainers = trainerRepository.findAllById(trainerIds);

        if (fetchedTrainers.size() != trainerIds.size()) {
            throw new EntityNotFoundException("One or more trainers were not found");
        }

        fetchedTrainee.getTrainers().clear();
        fetchedTrainee.getTrainers().addAll(fetchedTrainers);
    }
    @Transactional
    public Set<EmbeddedTrainerResponse> updateTrainersToTrainee(String username, Set<String> trainerUsernames){
        if  (trainerUsernames == null) {
            throw new InvalidRequestException("Trainer list cannot be null");
        }

        LOG.info("Updating trainers list for trainee with username: {}", username);

        Trainee fetchedTrainee = findTraineeByUsernameOrThrow(username);
        Set<Trainer> fetchedTrainers = trainerRepository.findAllByUserUsernameIn(trainerUsernames);

        if (fetchedTrainers.size() != trainerUsernames.size()) {
            throw new EntityNotFoundException("One or more trainers were not found");
        }

        fetchedTrainee.getTrainers().clear();
        fetchedTrainee.getTrainers().addAll(fetchedTrainers);

        return fetchedTrainers.stream()
                .map(TrainerMapper::toEmbeddedResponse)
                .collect(Collectors.toSet());
    }

    @Transactional
    public void activate(Long traineeId) {
        LOG.info("Activating trainee profile with id: {}", traineeId);

        Trainee fetchedTrainee = findTraineeByIdOrThrow(traineeId);

        if (Boolean.TRUE.equals(fetchedTrainee.getUser().getActive())) {
            throw new InvalidRequestException("Trainee is already active.");
        }

        fetchedTrainee.getUser().setActive(true);
    }

    @Transactional
    public void deactivate(Long traineeId) {
        LOG.info("Deactivating trainee profile with id: {}", traineeId);

        Trainee fetchedTrainee = findTraineeByIdOrThrow(traineeId);

        if (!Boolean.TRUE.equals(fetchedTrainee.getUser().getActive())) {
            throw new InvalidRequestException("Trainee is already deactivated.");
        }

        fetchedTrainee.getUser().setActive(false);
    }

    @Transactional
    public void setStatus(String username, Boolean activeStatus){
        if (activeStatus == null) {
            throw new InvalidRequestException("active status cannot be null");
        }

        LOG.info("Setting trainee with username: {} active status to '{}'",username, activeStatus ? "active"  : "inactive");

        Trainee fetchedTrainee = findTraineeByUsernameOrThrow(username);

        if (activeStatus.equals(fetchedTrainee.getUser().getActive())) {
            throw new InvalidRequestException(String.format("Trainee is already %s.", activeStatus ? "active"  : "inactive") );
        }
        fetchedTrainee.getUser().setActive(activeStatus);
    }

    private Trainee findTraineeByIdOrThrow(Long traineeId) {
        return traineeRepository.findById(traineeId)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found"));
    }

    private Trainee findTraineeByUsernameOrThrow(String username) {
        if (username == null || username.isBlank()) {
            throw new InvalidRequestException("Invalid username");
        }
        return traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found"));
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
        if (request.username() == null || request.username().isBlank()) {
            throw new InvalidRequestException("Username cannot be empty");
        }
        if (request.firstName() == null || request.firstName().isBlank()) {
            throw new InvalidRequestException("First name cannot be empty");
        }
        if (request.lastName() == null || request.lastName().isBlank()) {
            throw new InvalidRequestException("Last name cannot be empty");
        }
        if (request.isActive() == null){
            throw new InvalidRequestException("Trainee active status is mandatory");
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
}
