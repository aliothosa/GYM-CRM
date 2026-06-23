package com.elioth.epam.gymcrm.dto.mapper;

import com.elioth.epam.gymcrm.domain.User;
import com.elioth.epam.gymcrm.dto.request.CreateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.CreateTrainerRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTrainerRequest;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(CreateTraineeRequest request, String username, String password) {
        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);
        return user;
    }

    public User toUser(CreateTrainerRequest request, String username, String password) {
        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);
        return user;
    }

    public void updateUser(User user, UpdateTraineeRequest request) {
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
    }

    public void updateUser(User user, UpdateTrainerRequest request) {
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
    }
}
