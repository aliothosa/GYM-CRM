package com.elioth.epam.gymcrm.dto.mapper;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.Training;
import com.elioth.epam.gymcrm.domain.TrainingType;
import com.elioth.epam.gymcrm.domain.User;
import com.elioth.epam.gymcrm.dto.request.CreateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTraineeRequest;
import com.elioth.epam.gymcrm.dto.response.CreatedTraineeResponse;
import com.elioth.epam.gymcrm.dto.response.EmbeddedTraineeResponse;
import com.elioth.epam.gymcrm.dto.response.EmbeddedTrainerResponse;
import com.elioth.epam.gymcrm.dto.response.TraineeResponse;
import org.springframework.stereotype.Component;

@Component
public class TraineeMapper {

    public static Trainee toEntity(CreateTraineeRequest request, User user) {
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setBirthDate(request.birthDate());
        trainee.setAddress(request.address());
        return trainee;
    }

    public static void updateEntity(Trainee trainee, UpdateTraineeRequest request) {
        trainee.setBirthDate(request.birthDate());
        trainee.setAddress(request.address());
    }

    public static TraineeResponse toResponse(Trainee trainee) {
        if (trainee == null) {
            return null;
        }

        User user = trainee.getUser();
        if (user == null) {
            throw new IllegalStateException("Trainee must have an associated user");
        }

        return new TraineeResponse(
                trainee.getTraineeId(),
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getActive(),
                trainee.getBirthDate(),
                trainee.getAddress(),
                trainee.getTrainers().stream().map(TrainerMapper::toEmbeddedResponse).toList()
        );
    }

    public static CreatedTraineeResponse toCreatedResponse(Trainee trainee, String rawPassword) {
        if (trainee == null) {
            return null;
        }

        User user = trainee.getUser();
        if (user == null) {
            throw new IllegalStateException("Trainee must have an associated user");
        }

        return new CreatedTraineeResponse(
                trainee.getTraineeId(),
                user.getUsername(),
                rawPassword
        );
    }

    protected static EmbeddedTraineeResponse toEmbeddedResponse(Trainee trainee) {
        return new EmbeddedTraineeResponse(
                trainee.getUser().getUsername(),
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName()
        );
    }
}
