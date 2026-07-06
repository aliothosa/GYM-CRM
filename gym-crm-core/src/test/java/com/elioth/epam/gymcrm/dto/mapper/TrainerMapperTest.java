package com.elioth.epam.gymcrm.dto.mapper;

import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.TrainingType;
import com.elioth.epam.gymcrm.domain.User;
import com.elioth.epam.gymcrm.dto.response.CreatedTrainerResponse;
import com.elioth.epam.gymcrm.dto.response.TrainerResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainerMapperTest {

    @Test
    void shouldMapUserAndSpecializationToEntity() {
        User user = new User();
        user.setUsername("trainer.user");
        TrainingType specialization = new TrainingType();
        specialization.setName("Yoga");

        Trainer trainer = TrainerMapper.toEntity(user, specialization);

        assertEquals(user, trainer.getUser());
        assertEquals(specialization, trainer.getSpecialization());
    }

    @Test
    void shouldUpdateTrainerSpecialization() {
        Trainer trainer = new Trainer();
        TrainingType oldSpecialization = new TrainingType();
        oldSpecialization.setName("Cardio");
        trainer.setSpecialization(oldSpecialization);
        TrainingType newSpecialization = new TrainingType();
        newSpecialization.setName("Pilates");

        TrainerMapper.updateEntity(trainer, newSpecialization);

        assertEquals(newSpecialization, trainer.getSpecialization());
    }

    @Test
    void shouldMapTrainerToResponse() {
        User user = new User();
        user.setUserId(2L);
        user.setFirstName("Carl");
        user.setLastName("Lee");
        user.setUsername("carl.lee");
        user.setActive(true);
        TrainingType specialization = new TrainingType();
        specialization.setId(3L);
        specialization.setName("CrossFit");
        Trainer trainer = new Trainer();
        trainer.setTrainerId(1L);
        trainer.setUser(user);
        trainer.setSpecialization(specialization);

        TrainerResponse response = TrainerMapper.toResponse(trainer);

        assertEquals(1L, response.trainerId());
        assertEquals(2L, response.userId());
        assertEquals("Carl", response.firstName());
        assertEquals("Lee", response.lastName());
        assertEquals("carl.lee", response.username());
        assertTrue(response.active());
        assertEquals(3L, response.trainingTypeId());
        assertEquals("CrossFit", response.trainingTypeName());
    }

    @Test
    void shouldMapTrainerToCreatedResponse() {
        User user = new User();
        user.setUsername("new.trainer");
        user.setPassword("generatedPass");
        Trainer trainer = new Trainer();
        trainer.setTrainerId(7L);
        trainer.setUser(user);

        CreatedTrainerResponse response = TrainerMapper.toCreatedResponse(trainer);

        assertEquals(7L, response.trainerId());
        assertEquals("new.trainer", response.username());
        assertEquals("generatedPass", response.password());
    }

    @Test
    void shouldHandleNullSpecializationInResponse() {
        User user = new User();
        user.setUserId(2L);
        user.setFirstName("No");
        user.setLastName("Spec");
        user.setUsername("no.spec");
        user.setActive(false);
        Trainer trainer = new Trainer();
        trainer.setTrainerId(1L);
        trainer.setUser(user);

        TrainerResponse response = TrainerMapper.toResponse(trainer);

        assertNull(response.trainingTypeId());
        assertNull(response.trainingTypeName());
    }

    @Test
    void shouldReturnNullWhenTrainerIsNull() {
        assertNull(TrainerMapper.toResponse(null));
        assertNull(TrainerMapper.toCreatedResponse(null));
    }

    @Test
    void shouldThrowWhenTrainerHasNoAssociatedUser() {
        Trainer trainer = new Trainer();
        trainer.setTrainerId(1L);

        assertThrows(IllegalStateException.class, () -> TrainerMapper.toResponse(trainer));
        assertThrows(IllegalStateException.class, () -> TrainerMapper.toCreatedResponse(trainer));
    }
}
