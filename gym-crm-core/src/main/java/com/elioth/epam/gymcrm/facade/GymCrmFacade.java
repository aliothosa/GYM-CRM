package com.elioth.epam.gymcrm.facade;

import com.elioth.epam.gymcrm.auth.AuthSession;
import com.elioth.epam.gymcrm.auth.Role;
import com.elioth.epam.gymcrm.auth.SessionManager;
import com.elioth.epam.gymcrm.dto.request.ChangePasswordRequest;
import com.elioth.epam.gymcrm.dto.request.CreateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.CreateTrainerRequest;
import com.elioth.epam.gymcrm.dto.request.CreateTrainingRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTrainerRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTrainingRequest;
import com.elioth.epam.gymcrm.dto.response.CreatedTraineeResponse;
import com.elioth.epam.gymcrm.dto.response.CreatedTrainerResponse;
import com.elioth.epam.gymcrm.dto.response.TraineeResponse;
import com.elioth.epam.gymcrm.dto.response.TrainerResponse;
import com.elioth.epam.gymcrm.dto.response.TrainingResponse;
import com.elioth.epam.gymcrm.exception.UnauthorizedOperationException;
import com.elioth.epam.gymcrm.service.AuthService;
import com.elioth.epam.gymcrm.service.TraineeService;
import com.elioth.epam.gymcrm.service.TrainerService;
import com.elioth.epam.gymcrm.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class GymCrmFacade {

    private final AuthService authService;
    private final SessionManager sessionManager;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Autowired
    public GymCrmFacade( AuthService authService, SessionManager sessionManager,  TraineeService traineeService,  TrainerService trainerService, TrainingService trainingService) {
        this.authService = authService;
        this.sessionManager = sessionManager;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    // =========================
    // Authentication
    // =========================

    public AuthSession loginAsTrainee(String username, String password) {
        AuthSession session = authService.loginTrainee(username, password);
        sessionManager.login(session);
        return session;
    }

    public AuthSession loginAsTrainer(String username, String password) {
        AuthSession session = authService.loginTrainer(username, password);
        sessionManager.login(session);
        return session;
    }

    public void logout() {
        sessionManager.logout();
    }

    public boolean isAuthenticated() {
        return sessionManager.isAuthenticated();
    }

    public AuthSession getCurrentSession() {
        return sessionManager.getCurrentSession();
    }

    // =========================
    // Profile creation (no login required)
    // =========================

    public CreatedTraineeResponse createTraineeProfile(CreateTraineeRequest request) {
        return traineeService.createProfile(request);
    }

    public CreatedTrainerResponse createTrainerProfile(CreateTrainerRequest request) {
        return trainerService.createProfile(request);
    }

    // =========================
    // Trainee operations
    // =========================

    public TraineeResponse getTraineeProfile() {
        AuthSession session = requireRole(Role.TRAINEE);
        return traineeService.getProfileById(session.id());
    }

    public TraineeResponse getTraineeProfileByUsername(String username) {
        AuthSession session = requireRole(Role.TRAINEE);
        assertSameUsername(session, username);
        return traineeService.getProfileByUsername(username);
    }

    public TraineeResponse updateTraineeProfile(UpdateTraineeRequest request) {
        AuthSession session = requireRole(Role.TRAINEE);
        return traineeService.updateProfile(session.id(), request);
    }

    public void deleteTraineeProfile() {
        AuthSession session = requireRole(Role.TRAINEE);
        traineeService.deleteProfile(session.id());
        sessionManager.logout();
    }

    public TraineeResponse changeTraineePassword(ChangePasswordRequest request) {
        AuthSession session = requireRole(Role.TRAINEE);
        return traineeService.changePassword(session.id(), request);
    }

    public void activateTraineeProfile() {
        AuthSession session = requireRole(Role.TRAINEE);
        traineeService.activate(session.id());
    }

    public void deactivateTraineeProfile() {
        AuthSession session = requireRole(Role.TRAINEE);
        traineeService.deactivate(session.id());
    }

    public void updateTraineeTrainersList(List<Long> trainerIds) {
        AuthSession session = requireRole(Role.TRAINEE);
        traineeService.updateTrainersToTrainee(session.id(), trainerIds);
    }

    public List<TrainerResponse> getTrainersNotAssignedToTrainee() {
        AuthSession session = requireRole(Role.TRAINEE);
        return trainerService.getTrainersNotAssignedToTrainee(session.username());
    }

    public List<TrainingResponse> getTraineeTrainingsByCriteria(
            LocalDate from,
            LocalDate to,
            String trainerName,
            String trainingTypeName
    ) {
        AuthSession session = requireRole(Role.TRAINEE);
        return trainingService.getTrainingsByTraineeUsernameAndCriteria(
                session.username(),
                from,
                to,
                trainerName,
                trainingTypeName
        );
    }

    // =========================
    // Trainer operations
    // =========================

    public TrainerResponse getTrainerProfile() {
        AuthSession session = requireRole(Role.TRAINER);
        return trainerService.getProfileById(session.id());
    }

    public TrainerResponse getTrainerProfileByUsername(String username) {
        AuthSession session = requireRole(Role.TRAINER);
        assertSameUsername(session, username);
        return trainerService.getProfileByUsername(username);
    }

    public TrainerResponse updateTrainerProfile(UpdateTrainerRequest request) {
        AuthSession session = requireRole(Role.TRAINER);
        return trainerService.updateProfile(session.id(), request);
    }

    public TrainerResponse changeTrainerPassword(ChangePasswordRequest request) {
        AuthSession session = requireRole(Role.TRAINER);
        return trainerService.changePassword(session.id(), request);
    }

    public void activateTrainerProfile() {
        AuthSession session = requireRole(Role.TRAINER);
        trainerService.activate(session.id());
    }

    public void deactivateTrainerProfile() {
        AuthSession session = requireRole(Role.TRAINER);
        trainerService.deactivate(session.id());
    }

    public List<TrainerResponse> findTrainersBySpecializationName(String name) {
        requireRole(Role.TRAINER);
        return trainerService.findBySpecializationName(name);
    }

    // =========================
    // Training operations (trainer only)
    // =========================

    public TrainingResponse createTraining(CreateTrainingRequest request) {
        AuthSession session = requireRole(Role.TRAINER);
        if (!session.id().equals(request.trainerId())) {
            throw new UnauthorizedOperationException("Authenticated trainer must match the training trainer");
        }
        return trainingService.createTraining(session.id(), request);
    }

    public TrainingResponse updateTraining(Long trainingId, UpdateTrainingRequest request) {
        AuthSession session = requireRole(Role.TRAINER);
        return trainingService.updateTraining(session.id(), trainingId, request);
    }

    public void deleteTraining(Long trainingId) {
        AuthSession session = requireRole(Role.TRAINER);
        trainingService.deleteTraining(session.id(), trainingId);
    }

    public TrainingResponse getTrainingById(Long trainingId) {
        AuthSession session = requireRole(Role.TRAINER);
        return trainingService.getTrainingById(session.id(), trainingId);
    }

    public List<TrainingResponse> getTrainerTrainingsByCriteria(
            LocalDate from,
            LocalDate to,
            String traineeName
    ) {
        AuthSession session = requireRole(Role.TRAINER);
        return trainingService.getTrainingsByTrainerUsernameAndCriteria(
                session.username(),
                from,
                to,
                traineeName
        );
    }

    // =========================
    // Authorization helpers
    // =========================

    private AuthSession requireRole(Role role) {
        AuthSession session = sessionManager.getCurrentSession();
        if (session.role() != role) {
            throw new UnauthorizedOperationException("Operation requires role: " + role);
        }
        return session;
    }

    private void assertSameUsername(AuthSession session, String username) {
        if (!session.username().equals(username)) {
            throw new UnauthorizedOperationException("Cannot access another user's profile");
        }
    }
}
