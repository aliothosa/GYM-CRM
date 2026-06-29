package com.elioth.epam.gymcrm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "gymcrm.cli.enabled=false")
class GymCrmApplicationTests {

    @Test
    void contextLoads() {
    }

}
