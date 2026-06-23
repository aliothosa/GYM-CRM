package com.elioth.epam.gymcrm.dto.mapper;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.Training;
import com.elioth.epam.gymcrm.domain.TrainingType;
import com.elioth.epam.gymcrm.domain.User;
import com.elioth.epam.gymcrm.dto.request.CreateTrainingRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTrainingRequest;
import com.elioth.epam.gymcrm.dto.response.TrainingResponse;
import org.springframework.stereotype.Component;

@Component
public class TrainingMapper {

    public Training toEntity(
            CreateTrainingRequest request,
            Trainee trainee,
            Trainer trainer,
            TrainingType trainingType
    ) {
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setType(trainingType);
        training.setName(request.trainingName());
        training.setDate(request.trainingDate());
        training.setDurationInMinutes(Math.toIntExact(request.duration()));
        return training;
    }

    public void updateEntity(Training training, UpdateTrainingRequest request) {
        training.setName(request.name());
        training.setDate(request.date());
        training.setDurationInMinutes(Math.toIntExact(request.duration()));
    }

    public TrainingResponse toResponse(Training training) {
        Trainee trainee = training.getTrainee();
        Trainer trainer = training.getTrainer();
        TrainingType trainingType = training.getType();
        User traineeUser = trainee.getUser();
        User trainerUser = trainer.getUser();

        return new TrainingResponse(
                training.getTrainingId(),
                trainee.getTraineeId(),
                traineeUser.getUsername(),
                traineeUser.getFirstName(),
                traineeUser.getLastName(),
                trainer.getTrainerId(),
                trainerUser.getUsername(),
                trainerUser.getFirstName(),
                trainerUser.getLastName(),
                trainingType.getTrainingTypeId(),
                trainingType.getName(),
                training.getName(),
                training.getDate(),
                Long.valueOf(training.getDurationInMinutes())
        );
    }
}
