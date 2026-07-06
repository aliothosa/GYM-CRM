package com.elioth.epam.gymcrm.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoleTest {

    @Test
    void shouldContainTraineeAndTrainerValues() {
        assertEquals(2, Role.values().length);
        assertEquals(Role.TRAINEE, Role.valueOf("TRAINEE"));
        assertEquals(Role.TRAINER, Role.valueOf("TRAINER"));
    }
}
