package com.elioth.epam.gymcrm.utils;

import com.elioth.epam.gymcrm.domain.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UtilsTest {

    @Test
    void shouldGenerateUsernameInBaseFormat() {
        User user = buildUser("John", "Smith");

        String username = Utils.usernameGenerator(user, List.of());

        assertEquals("John.Smith", username);
    }

    @Test
    void shouldGenerateUsernameWithSerialSuffixForDuplicateName() {
        User existing = buildUser("John", "Smith");
        existing.setUsername("John.Smith");
        User duplicate = buildUser("John", "Smith");

        String username = Utils.usernameGenerator(duplicate, List.of(existing));

        assertEquals("John.Smith1", username);
    }

    @Test
    void shouldGenerateUsernameFromNamesAndCount() {
        assertEquals("John.Smith", Utils.usernameGenerator("John", "Smith", 0));
        assertEquals("John.Smith2", Utils.usernameGenerator("John", "Smith", 2));
    }

    @Test
    void shouldGenerateNonNullPassword() {
        assertNotNull(Utils.generateRandomPassword());
    }

    @Test
    void shouldGeneratePasswordWithExactlyTenCharacters() {
        assertEquals(10, Utils.generateRandomPassword().length());
    }

    @Test
    void shouldGenerateDifferentPasswordsAcrossMultipleCalls() {
        List<String> passwords = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            passwords.add(Utils.generateRandomPassword());
        }

        assertTrue(passwords.stream().distinct().count() > 1,
                "Expected at least some variation in generated passwords");
    }

    private User buildUser(String firstName, String lastName) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }
}
