package com.elioth.epam.gymcrm.dto.mapper;

import com.elioth.epam.gymcrm.domain.Address;
import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.User;
import com.elioth.epam.gymcrm.dto.request.CreateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTraineeRequest;
import com.elioth.epam.gymcrm.dto.response.CreatedTraineeResponse;
import com.elioth.epam.gymcrm.dto.response.TraineeResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TraineeMapperTest {

    @Test
    void shouldMapCreateTraineeRequestToEntity() {
        Address address = new Address("Main St", "Boston", "MA", "02101", 42);
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        CreateTraineeRequest request = new CreateTraineeRequest("John", "Doe", birthDate, address);
        User user = new User();
        user.setUsername("john.doe");

        Trainee trainee = TraineeMapper.toEntity(request, user);

        assertEquals(user, trainee.getUser());
        assertEquals(birthDate, trainee.getBirthDate());
        assertEquals(address, trainee.getAddress());
    }

    @Test
    void shouldUpdateTraineeEntityFromRequest() {
        Trainee trainee = new Trainee();
        Address newAddress = new Address("Oak Ave", "Cambridge", "MA", "02139", 10);
        LocalDate newBirthDate = LocalDate.of(1995, 12, 1);
        UpdateTraineeRequest request = new UpdateTraineeRequest("Updated", "Name", newBirthDate, newAddress);

        TraineeMapper.updateEntity(trainee, request);

        assertEquals(newBirthDate, trainee.getBirthDate());
        assertEquals(newAddress, trainee.getAddress());
    }

    @Test
    void shouldMapTraineeToResponse() {
        User user = new User();
        user.setUserId(20L);
        user.setFirstName("Alice");
        user.setLastName("Wonder");
        user.setUsername("alice.wonder");
        user.setActive(true);
        Address address = new Address("Pine Rd", "Denver", "CO", "80202", 15);
        LocalDate birthDate = LocalDate.of(1988, 1, 5);
        Trainee trainee = new Trainee();
        trainee.setTraineeId(10L);
        trainee.setUser(user);
        trainee.setBirthDate(birthDate);
        trainee.setAddress(address);

        TraineeResponse response = TraineeMapper.toResponse(trainee);

        assertEquals(10L, response.traineeId());
        assertEquals(20L, response.userId());
        assertEquals("Alice", response.firstName());
        assertEquals("Wonder", response.lastName());
        assertEquals("alice.wonder", response.username());
        assertTrue(response.active());
        assertEquals(birthDate, response.birthDate());
        assertEquals(address, response.address());
    }

    @Test
    void shouldMapTraineeToCreatedResponse() {
        User user = new User();
        user.setUsername("new.trainee");
        user.setPassword("generatedPass");
        Trainee trainee = new Trainee();
        trainee.setTraineeId(5L);
        trainee.setUser(user);

        CreatedTraineeResponse response = TraineeMapper.toCreatedResponse(trainee);

        assertEquals(5L, response.traineeId());
        assertEquals("new.trainee", response.username());
        assertEquals("generatedPass", response.password());
    }

    @Test
    void shouldReturnNullWhenTraineeIsNull() {
        assertNull(TraineeMapper.toResponse(null));
        assertNull(TraineeMapper.toCreatedResponse(null));
    }

    @Test
    void shouldThrowWhenTraineeHasNoAssociatedUser() {
        Trainee trainee = new Trainee();
        trainee.setTraineeId(1L);

        assertThrows(IllegalStateException.class, () -> TraineeMapper.toResponse(trainee));
        assertThrows(IllegalStateException.class, () -> TraineeMapper.toCreatedResponse(trainee));
    }
}
