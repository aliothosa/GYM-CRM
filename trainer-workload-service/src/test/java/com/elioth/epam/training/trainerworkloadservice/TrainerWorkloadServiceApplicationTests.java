package com.elioth.epam.training.trainerworkloadservice;

import com.elioth.epam.workload.TrainerWorkloadServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = TrainerWorkloadServiceApplication.class,
        properties = {
                "gymcrm.jwt.secret=test-only-jwt-secret-with-at-least-32-characters",
                "eureka.client.enabled=false"
        }
)
class TrainerWorkloadServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
