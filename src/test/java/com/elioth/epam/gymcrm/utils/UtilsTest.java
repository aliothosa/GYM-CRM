package com.elioth.epam.gymcrm.utils;

import com.elioth.epam.gymcrm.domain.Trainee;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UtilsTest {

    @Test
    void shouldGenerateUsernameInBaseFormat() {
        // Given
        Trainee trainee = buildTrainee("John", "Smith");

        // When
        String username = Utils.usernameGenerator(trainee, List.of());

        // Then
        assertEquals("John.Smith", username);
    }

    @Test
    void shouldGenerateUsernameWithSerialSuffixForDuplicateName() {
        // Given
        Trainee existing = buildTrainee("John", "Smith");
        existing.setUsername("John.Smith");
        Trainee duplicate = buildTrainee("John", "Smith");

        // When
        String username = Utils.usernameGenerator(duplicate, List.of(existing));

        // Then
        assertEquals("John.Smith1", username);
    }

    @Test
    void shouldGenerateNonNullPassword() {
        // When
        String password = Utils.generateRandomPassword();

        // Then
        assertNotNull(password);
    }

    @Test
    void shouldGeneratePasswordWithExactlyTenCharacters() {
        // When
        String password = Utils.generateRandomPassword();

        // Then
        assertEquals(10, password.length());
    }

    @Test
    void shouldGenerateDifferentPasswordsAcrossMultipleCalls() {
        // Given
        List<String> passwords = new ArrayList<>();

        // When
        for (int i = 0; i < 20; i++) {
            passwords.add(Utils.generateRandomPassword());
        }

        // Then
        assertTrue(passwords.stream().distinct().count() > 1,
                "Expected at least some variation in generated passwords");
    }

    private Trainee buildTrainee(String firstName, String lastName) {
        Trainee trainee = new Trainee();
        trainee.setId(UUID.randomUUID());
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        return trainee;
    }
}
