package com.elioth.epam.gymcrm.dto.mapper;

import com.elioth.epam.gymcrm.domain.User;
import com.elioth.epam.gymcrm.dto.request.CreateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.CreateTrainerRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTrainerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void shouldMapCreateTraineeRequestToUser() {
        CreateTraineeRequest request = new CreateTraineeRequest("John", "Doe", null, null);

        User user = userMapper.toUser(request, "john.doe", "password123");

        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john.doe", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertTrue(user.getActive());
    }

    @Test
    void shouldMapCreateTrainerRequestToUser() {
        CreateTrainerRequest request = new CreateTrainerRequest("Jane", "Smith", 1L);

        User user = userMapper.toUser(request, "jane.smith", "password456");

        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("jane.smith", user.getUsername());
        assertEquals("password456", user.getPassword());
        assertTrue(user.getActive());
    }

    @Test
    void shouldUpdateUserFromUpdateTraineeRequest() {
        User user = new User();
        user.setFirstName("Old");
        user.setLastName("Name");
        UpdateTraineeRequest request = new UpdateTraineeRequest("New", "Trainee", null, null);

        userMapper.updateUser(user, request);

        assertEquals("New", user.getFirstName());
        assertEquals("Trainee", user.getLastName());
    }

    @Test
    void shouldUpdateUserFromUpdateTrainerRequest() {
        User user = new User();
        user.setFirstName("Old");
        user.setLastName("Name");
        UpdateTrainerRequest request = new UpdateTrainerRequest("New", "Trainer", 2L);

        userMapper.updateUser(user, request);

        assertEquals("New", user.getFirstName());
        assertEquals("Trainer", user.getLastName());
    }
}
