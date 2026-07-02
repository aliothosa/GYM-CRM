package com.elioth.epam.gymcrm.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig(classes = GymConfig.class)
@TestPropertySource(properties = "gymcrm.cli.enabled=false")
class GymConfigTest {

    @Autowired
    private GymConfig gymConfig;

    @Test
    void shouldLoadGymConfigInContext() {
        assertNotNull(gymConfig);
    }
}
