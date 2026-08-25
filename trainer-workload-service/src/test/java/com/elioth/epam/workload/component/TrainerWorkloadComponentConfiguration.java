package com.elioth.epam.workload.component;

import com.elioth.epam.workload.TrainerWorkloadServiceApplication;
import com.elioth.epam.workload.repository.TrainerWorkloadRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@CucumberContextConfiguration
@SpringBootTest(
        classes = TrainerWorkloadServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "gymcrm.jwt.secret=test-only-jwt-secret-with-at-least-32-characters",
                "eureka.client.enabled=false",
                "spring.jms.listener.auto-startup=false",
                "spring.data.mongodb.auto-index-creation=false",
                "spring.autoconfigure.exclude=org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration,org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration,org.springframework.boot.data.mongodb.autoconfigure.DataMongoRepositoriesAutoConfiguration"
        }
)
public class TrainerWorkloadComponentConfiguration {

    @MockitoBean
    TrainerWorkloadRepository trainerWorkloadRepository;
}
