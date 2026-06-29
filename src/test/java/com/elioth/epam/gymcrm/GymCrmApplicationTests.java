package com.elioth.epam.gymcrm;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "gymcrm.cli.enabled=false")
@Disabled("Requires PostgreSQL or H2 test profile; enable after test database configuration is added.")
class GymCrmApplicationTests {

    @Test
    void contextLoads() {
        // TODO: ensure PostgreSQL is running (scripts/run-postgres.sh) OR add H2 + application-test.properties
        // TODO: remove @Disabled on this class and run mvn test -Dtest=GymCrmApplicationTests
    }

}
