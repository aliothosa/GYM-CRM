package com.elioth.epam.gymcrm.service;

import com.elioth.epam.gymcrm.dao.TrainerDao;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.TrainingType;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.exception.InvalidEntityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        trainerService.setTrainerDao(trainerDao);
    }

    @Test
    void shouldCreateTrainerWithGeneratedUsername() {
        // Given
        Trainer trainer = buildTrainer(null, "Robert", "Brown");
        when(trainerDao.findAll()).thenReturn(List.of());
        when(trainerDao.create(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Trainer created = trainerService.createProfile(trainer);

        // Then
        assertEquals("Robert.Brown", created.getUsername());
        assertNotNull(created.getId());
        assertTrue(created.isActive());
    }

    @Test
    void shouldCreateTrainerWithPasswordOfExactlyTenCharacters() {
        // Given
        Trainer trainer = buildTrainer(null, "Robert", "Brown");
        when(trainerDao.findAll()).thenReturn(List.of());
        when(trainerDao.create(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Trainer created = trainerService.createProfile(trainer);

        // Then
        assertNotNull(created.getPassword());
        assertEquals(10, created.getPassword().length());
    }

    @Test
    void shouldCreateTrainerWithSerialSuffixWhenDuplicateNameExists() {
        // Given
        Trainer existing = buildTrainer(UUID.randomUUID(), "Robert", "Brown");
        existing.setUsername("Robert.Brown");
        Trainer duplicate = buildTrainer(null, "Robert", "Brown");
        when(trainerDao.findAll()).thenReturn(List.of(existing));
        when(trainerDao.create(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Trainer created = trainerService.createProfile(duplicate);

        // Then
        assertEquals("Robert.Brown1", created.getUsername());
    }

    @Test
    void shouldUpdateExistingTrainer() {
        // Given
        UUID id = UUID.randomUUID();
        Trainer existing = buildTrainer(id, "Robert", "Brown");
        existing.setUsername("Robert.Brown");
        existing.setPassword("secretpass");
        existing.setActive(true);

        Trainer updateRequest = buildTrainer(id, "Bob", "Brown");
        updateRequest.setSpecialization(TrainingType.MOBILITY);

        when(trainerDao.findById(id)).thenReturn(Optional.of(existing));
        when(trainerDao.update(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Trainer updated = trainerService.updateProfile(updateRequest);

        // Then
        assertEquals("Bob", updated.getFirstName());
        assertEquals(TrainingType.MOBILITY, updated.getSpecialization());
        assertEquals("Robert.Brown", updated.getUsername());
        assertEquals("secretpass", updated.getPassword());
        verify(trainerDao).update(updateRequest);
    }

    @Test
    void shouldReturnExistingTrainerWhenGetProfileById() {
        // Given
        UUID id = UUID.randomUUID();
        Trainer trainer = buildTrainer(id, "Robert", "Brown");
        when(trainerDao.findById(id)).thenReturn(Optional.of(trainer));

        // When
        Trainer found = trainerService.getProfileById(id);

        // Then
        assertEquals(trainer, found);
    }

    @Test
    void shouldThrowInvalidEntityExceptionWhenTrainerIsNullOnCreate() {
        // When / Then
        assertThrows(InvalidEntityException.class, () -> trainerService.createProfile(null));
        verify(trainerDao, never()).create(any());
    }

    @Test
    void shouldThrowInvalidEntityExceptionWhenSpecializationIsNullOnCreate() {
        // Given
        Trainer trainer = buildTrainer(null, "Robert", "Brown");
        trainer.setSpecialization(null);

        // When / Then
        assertThrows(InvalidEntityException.class, () -> trainerService.createProfile(trainer));
    }

    @Test
    void shouldThrowInvalidEntityExceptionWhenTrainerIsNullOnUpdate() {
        // When / Then
        assertThrows(InvalidEntityException.class, () -> trainerService.updateProfile(null));
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUpdateNonExistingTrainer() {
        // Given
        UUID id = UUID.randomUUID();
        Trainer trainer = buildTrainer(id, "Robert", "Brown");
        when(trainerDao.findById(id)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(EntityNotFoundException.class, () -> trainerService.updateProfile(trainer));
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenGetProfileByIdDoesNotExist() {
        // Given
        UUID id = UUID.randomUUID();
        when(trainerDao.findById(id)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(EntityNotFoundException.class, () -> trainerService.getProfileById(id));
    }

    private Trainer buildTrainer(UUID id, String firstName, String lastName) {
        Trainer trainer = new Trainer();
        trainer.setId(id);
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setSpecialization(TrainingType.WEIGHT);
        return trainer;
    }
}
