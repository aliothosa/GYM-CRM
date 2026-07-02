package com.elioth.epam.gymcrm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "gymcrm.cli.enabled=false")
@ActiveProfiles("test")
class GymCrmApplicationTests {

    @Test
    void contextLoads() {
    }
}
