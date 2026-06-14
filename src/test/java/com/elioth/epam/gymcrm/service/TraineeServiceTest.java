package com.elioth.epam.gymcrm.service;

import com.elioth.epam.gymcrm.dao.TraineeDao;
import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.exception.InvalidEntityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class TraineeServiceTest {

    @Mock
    private TraineeDao traineeDao;

    @InjectMocks
    private TraineeService traineeService;

    @BeforeEach
    void setUp() {
        traineeService.setDao(traineeDao);
    }

    @Test
    void shouldCreateTraineeWithGeneratedUsername() {
        // Given
        Trainee trainee = buildTrainee(null, "John", "Smith");
        when(traineeDao.findAll()).thenReturn(List.of());
        when(traineeDao.create(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Trainee created = traineeService.createProfile(trainee);

        // Then
        assertEquals("John.Smith", created.getUsername());
        assertNotNull(created.getId());
        assertTrue(created.isActive());
        verify(traineeDao).create(any(Trainee.class));
    }

    @Test
    void shouldCreateTraineeWithPasswordOfExactlyTenCharacters() {
        // Given
        Trainee trainee = buildTrainee(null, "John", "Smith");
        when(traineeDao.findAll()).thenReturn(List.of());
        when(traineeDao.create(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Trainee created = traineeService.createProfile(trainee);

        // Then
        assertNotNull(created.getPassword());
        assertEquals(10, created.getPassword().length());
    }

    @Test
    void shouldCreateTraineeWithSerialSuffixWhenDuplicateNameExists() {
        // Given
        Trainee existing = buildTrainee(UUID.randomUUID(), "John", "Smith");
        existing.setUsername("John.Smith");
        Trainee duplicate = buildTrainee(null, "John", "Smith");
        when(traineeDao.findAll()).thenReturn(List.of(existing));
        when(traineeDao.create(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Trainee created = traineeService.createProfile(duplicate);

        // Then
        assertEquals("John.Smith1", created.getUsername());
    }

    @Test
    void shouldUpdateExistingTrainee() {
        // Given
        UUID id = UUID.randomUUID();
        Trainee existing = buildTrainee(id, "John", "Smith");
        existing.setUsername("John.Smith");
        existing.setPassword("secretpass");
        existing.setActive(true);

        Trainee updateRequest = buildTrainee(id, "Jonathan", "Smith");

        when(traineeDao.findById(id)).thenReturn(Optional.of(existing));
        when(traineeDao.update(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Trainee updated = traineeService.updateProfile(updateRequest);

        // Then
        assertEquals("Jonathan", updated.getFirstName());
        assertEquals("John.Smith", updated.getUsername());
        assertEquals("secretpass", updated.getPassword());
        assertTrue(updated.isActive());
        verify(traineeDao).update(updateRequest);
    }

    @Test
    void shouldDeleteTraineeById() {
        // Given
        UUID id = UUID.randomUUID();
        Trainee existing = buildTrainee(id, "John", "Smith");
        when(traineeDao.findById(id)).thenReturn(Optional.of(existing));

        // When
        traineeService.deleteProfile(id);

        // Then
        verify(traineeDao).delete(id);
    }

    @Test
    void shouldReturnExistingTraineeWhenGetProfileById() {
        // Given
        UUID id = UUID.randomUUID();
        Trainee trainee = buildTrainee(id, "John", "Smith");
        when(traineeDao.findById(id)).thenReturn(Optional.of(trainee));

        // When
        Trainee found = traineeService.getProfileById(id);

        // Then
        assertEquals(trainee, found);
    }

    @Test
    void shouldThrowInvalidEntityExceptionWhenTraineeIsNullOnCreate() {
        // When / Then
        assertThrows(InvalidEntityException.class, () -> traineeService.createProfile(null));
        verify(traineeDao, never()).create(any());
    }

    @Test
    void shouldThrowInvalidEntityExceptionWhenFirstNameIsBlankOnCreate() {
        // Given
        Trainee trainee = buildTrainee(null, " ", "Smith");

        // When / Then
        assertThrows(InvalidEntityException.class, () -> traineeService.createProfile(trainee));
    }

    @Test
    void shouldThrowInvalidEntityExceptionWhenTraineeIsNullOnUpdate() {
        // When / Then
        assertThrows(InvalidEntityException.class, () -> traineeService.updateProfile(null));
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUpdateNonExistingTrainee() {
        // Given
        UUID id = UUID.randomUUID();
        Trainee trainee = buildTrainee(id, "John", "Smith");
        when(traineeDao.findById(id)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(EntityNotFoundException.class, () -> traineeService.updateProfile(trainee));
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenDeleteNonExistingTrainee() {
        // Given
        UUID id = UUID.randomUUID();
        when(traineeDao.findById(id)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(EntityNotFoundException.class, () -> traineeService.deleteProfile(id));
        verify(traineeDao, never()).delete(id);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenGetProfileByIdDoesNotExist() {
        // Given
        UUID id = UUID.randomUUID();
        when(traineeDao.findById(id)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(EntityNotFoundException.class, () -> traineeService.getProfileById(id));
    }

    @Test
    void shouldAssignGeneratedIdOnCreate() {
        // Given
        Trainee trainee = buildTrainee(null, "John", "Smith");
        when(traineeDao.findAll()).thenReturn(List.of());
        when(traineeDao.create(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        traineeService.createProfile(trainee);

        // Then
        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeDao).create(captor.capture());
        assertNotNull(captor.getValue().getId());
    }

    private Trainee buildTrainee(UUID id, String firstName, String lastName) {
        Trainee trainee = new Trainee();
        trainee.setId(id);
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        return trainee;
    }
}
