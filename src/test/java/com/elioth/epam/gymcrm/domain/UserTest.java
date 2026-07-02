package com.elioth.epam.gymcrm.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    @Test
    void shouldAllowSettingIdentityAndCredentials() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("john.doe");
        user.setPassword("secret");
        user.setActive(true);

        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john.doe", user.getUsername());
        assertEquals("secret", user.getPassword());
        assertTrue(user.getActive());
    }

    @Test
    void shouldHaveNullUserIdBeforePersistence() {
        User user = new User();

        assertNull(user.getUserId());
    }
}
