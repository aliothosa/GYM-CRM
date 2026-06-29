package com.elioth.epam.gymcrm.config;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Lightweight config test for {@link GymConfig}.
 * GymConfig references classpath:application.yaml — add a test yaml or adjust before enabling.
 */
@Disabled("Practice skeleton - enable after application.yaml exists or PropertySource is adjusted")
class GymConfigTest {

    @Test
    @Disabled("Practice skeleton")
    void shouldLoadGymConfigInContext() {
        // Arrange
        // TODO: use @SpringBootTest(properties = "gymcrm.cli.enabled=false") if full context needed
        // TODO: or test GymConfig as @Configuration class with @ContextConfiguration

        // Assert
        // TODO: assert context contains GymConfig bean
    }
}
