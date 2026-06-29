package com.elioth.epam.gymcrm.dto.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Practice skeleton - implement one test at a time, then remove @Disabled from class")
class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldMapCreateTraineeRequestToUser() {
        // Arrange
        // TODO: create CreateTraineeRequest with firstName and lastName

        // Act
        // TODO: User user = userMapper.toUser(request, "username", "password");

        // Assert
        // TODO: assert firstName, lastName, username, password, and active=true
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldMapCreateTrainerRequestToUser() {
        // Arrange
        // TODO: create CreateTrainerRequest with firstName and lastName

        // Act
        // TODO: User user = userMapper.toUser(request, "username", "password");

        // Assert
        // TODO: assert firstName, lastName, username, password, and active=true
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldUpdateUserFromUpdateTraineeRequest() {
        // Arrange
        // TODO: create User and UpdateTraineeRequest

        // Act
        // TODO: userMapper.updateUser(user, request);

        // Assert
        // TODO: assert firstName and lastName are updated
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldUpdateUserFromUpdateTrainerRequest() {
        // Arrange
        // TODO: create User and UpdateTrainerRequest

        // Act
        // TODO: userMapper.updateUser(user, request);

        // Assert
        // TODO: assert firstName and lastName are updated
    }
}
