package com.elioth.epam.gymcrm.dto.mapper;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.User;
import com.elioth.epam.gymcrm.dto.request.CreateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTraineeRequest;
import com.elioth.epam.gymcrm.dto.response.CreatedTraineeResponse;
import com.elioth.epam.gymcrm.dto.response.TraineeResponse;
import org.springframework.stereotype.Component;

@Component
public class TraineeMapper {

    public Trainee toEntity(CreateTraineeRequest request, User user) {
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setBirthDate(request.birthDate());
        trainee.setAddress(request.address());
        return trainee;
    }

    public void updateEntity(Trainee trainee, UpdateTraineeRequest request) {
        trainee.setBirthDate(request.birthDay());
        trainee.setAddress(request.address());
    }

    public TraineeResponse toResponse(Trainee trainee) {
        User user = trainee.getUser();

        return new TraineeResponse(
                trainee.getTraineeId(),
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.isActive(),
                trainee.getBirthDate(),
                trainee.getAddress()
        );
    }

    public CreatedTraineeResponse toCreatedResponse(Trainee trainee) {
        User user = trainee.getUser();

        return new CreatedTraineeResponse(
                trainee.getTraineeId(),
                user.getUsername(),
                user.getPassword()
        );
    }
}
