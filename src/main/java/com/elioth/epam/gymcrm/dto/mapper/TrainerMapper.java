package com.elioth.epam.gymcrm.dto.mapper;

import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.TrainingType;
import com.elioth.epam.gymcrm.domain.User;
import com.elioth.epam.gymcrm.dto.response.CreatedTrainerResponse;
import com.elioth.epam.gymcrm.dto.response.TrainerResponse;
import org.springframework.stereotype.Component;

@Component
public class TrainerMapper {

    public static Trainer toEntity(User user, TrainingType specialization) {
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(specialization);
        return trainer;
    }

    public static void updateEntity(Trainer trainer, TrainingType specialization) {
        trainer.setSpecialization(specialization);
    }

    public static TrainerResponse toResponse(Trainer trainer) {
        if (trainer == null) {
            return null;
        }

        User user = trainer.getUser();
        if (user == null) {
            throw new IllegalStateException("Trainer must have an associated user");
        }

        TrainingType specialization = trainer.getSpecialization();

        return new TrainerResponse(
                trainer.getTrainerId(),
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getActive(),
                specialization != null ? specialization.getId() : null,
                specialization != null ? specialization.getName() : null
        );
    }

    public static CreatedTrainerResponse toCreatedResponse(Trainer trainer) {
        if (trainer == null) {
            return null;
        }

        User user = trainer.getUser();
        if (user == null) {
            throw new IllegalStateException("Trainer must have an associated user");
        }

        return new CreatedTrainerResponse(
                trainer.getTrainerId(),
                user.getUsername(),
                user.getPassword()
        );
    }
}
