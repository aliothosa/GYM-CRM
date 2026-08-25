package com.elioth.epam.gymcrm.component;

import com.elioth.epam.gymcrm.GymCrmApiApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@CucumberContextConfiguration
@SpringBootTest(
        classes = GymCrmApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:gymcrm-cucumber;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.sql.init.mode=never",
                "gymcrm.jwt.secret=test-only-jwt-secret-with-at-least-32-characters",
                "eureka.client.enabled=false",
                "spring.jms.listener.auto-startup=false",
                "spring.autoconfigure.exclude=org.springframework.boot.jms.autoconfigure.JmsAutoConfiguration,org.springframework.boot.activemq.autoconfigure.ActiveMQAutoConfiguration"
        }
)
public class GymCrmApiComponentConfiguration {

    @MockitoBean
    JmsTemplate jmsTemplate;
}
