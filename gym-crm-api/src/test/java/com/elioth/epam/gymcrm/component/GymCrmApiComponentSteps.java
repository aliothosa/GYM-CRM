package com.elioth.epam.gymcrm.component;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.TrainingType;
import com.elioth.epam.gymcrm.domain.User;
import com.elioth.epam.gymcrm.messaging.TrainerWorkloadMessage;
import com.elioth.epam.gymcrm.repository.TraineeRepository;
import com.elioth.epam.gymcrm.repository.TrainerRepository;
import com.elioth.epam.gymcrm.repository.TrainingRepository;
import com.elioth.epam.gymcrm.repository.TrainingTypeRepository;
import com.elioth.epam.gymcrm.repository.UserRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

public class GymCrmApiComponentSteps {

    @Autowired private WebApplicationContext webApplicationContext;
    @Autowired private JmsTemplate jmsTemplate;
    @Autowired private TrainingRepository trainingRepository;
    @Autowired private TraineeRepository traineeRepository;
    @Autowired private TrainerRepository trainerRepository;
    @Autowired private TrainingTypeRepository trainingTypeRepository;
    @Autowired private UserRepository userRepository;

    private MockMvc mockMvc;
    private org.springframework.test.web.servlet.MvcResult response;

    @Before
    public void resetComponentState() {
        mockMvc = webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        trainingRepository.deleteAll();
        traineeRepository.deleteAll();
        trainerRepository.deleteAll();
        trainingTypeRepository.deleteAll();
        userRepository.deleteAll();
        reset(jmsTemplate);
    }

    @Given("an existing trainer {string} and trainee {string}")
    public void existingTrainerAndTrainee(String trainerUsername, String traineeUsername) {
        TrainingType specialization = new TrainingType();
        specialization.setName("FITNESS");
        trainingTypeRepository.save(specialization);

        Trainer trainer = new Trainer();
        trainer.setUser(user(trainerUsername, "Trainer", "Component"));
        trainer.setSpecialization(specialization);
        trainerRepository.save(trainer);

        Trainee trainee = new Trainee();
        trainee.setUser(user(traineeUsername, "Trainee", "Component"));
        trainee.setBirthDate(LocalDate.of(1995, 1, 1));
        traineeRepository.save(trainee);
    }

    @When("the authenticated client creates a {int}-minute training on {string} for trainer {string} and trainee {string}")
    public void authenticatedClientCreatesTraining(int duration, String date, String trainerUsername, String traineeUsername)
            throws Exception {
        response = mockMvc.perform(post("/trainings")
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"traineeUsername":"%s","trainerUsername":"%s","trainingName":"Component training","date":"%s","durationInMinutes":%d}
                                """.formatted(traineeUsername, trainerUsername, date, duration)))
                .andReturn();
    }

    @Then("the training is accepted")
    public void trainingIsAccepted() {
        Assertions.assertEquals(200, response.getResponse().getStatus());
        Assertions.assertEquals(1, trainingRepository.count());
    }

    @Then("an ADD workload message for trainer {string} with {int} minutes is sent")
    public void workloadMessageIsSent(String trainerUsername, int duration) {
        ArgumentCaptor<TrainerWorkloadMessage> message = ArgumentCaptor.forClass(TrainerWorkloadMessage.class);
        verify(jmsTemplate).convertAndSend(eq("trainer.workload"), message.capture(), any(MessagePostProcessor.class));
        Assertions.assertEquals(trainerUsername, message.getValue().trainerUsername());
        Assertions.assertEquals((long) duration, message.getValue().trainingDurationMinutes());
        Assertions.assertEquals("ADD", message.getValue().action());
    }

    @Then("the training is rejected as invalid")
    public void trainingIsRejectedAsInvalid() {
        Assertions.assertEquals(400, response.getResponse().getStatus());
        Assertions.assertEquals(0, trainingRepository.count());
    }

    @Then("no workload message is sent")
    public void noWorkloadMessageIsSent() {
        verifyNoInteractions(jmsTemplate);
    }

    private User user(String username, String firstName, String lastName) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword("password");
        user.setActive(true);
        return user;
    }
}
