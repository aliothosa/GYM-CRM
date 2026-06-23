package com.elioth.epam.gymcrm.dto.mapper;

import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.TrainingType;
import com.elioth.epam.gymcrm.domain.User;
import com.elioth.epam.gymcrm.dto.response.CreatedTrainerResponse;
import com.elioth.epam.gymcrm.dto.response.TrainerResponse;
import org.springframework.stereotype.Component;

@Component
public class TrainerMapper {

    public Trainer toEntity(User user, TrainingType specialization) {
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(specialization);
        return trainer;
    }

    public void updateEntity(Trainer trainer, TrainingType specialization) {
        trainer.setSpecialization(specialization);
    }

    public TrainerResponse toResponse(Trainer trainer) {
        User user = trainer.getUser();
        TrainingType specialization = trainer.getSpecialization();

        return new TrainerResponse(
                trainer.getTrainerId(),
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.isActive(),
                specialization.getTrainingTypeId(),
                specialization.getName()
        );
    }

    public CreatedTrainerResponse toCreatedResponse(Trainer trainer) {
        User user = trainer.getUser();

        return new CreatedTrainerResponse(
                trainer.getTrainerId(),
                user.getUsername(),
                user.getPassword()
        );
    }
}
