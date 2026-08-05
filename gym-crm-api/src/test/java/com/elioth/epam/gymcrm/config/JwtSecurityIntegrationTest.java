package com.elioth.epam.gymcrm.config;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.User;
import com.elioth.epam.gymcrm.repository.TraineeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
@ActiveProfiles("test")
class JwtSecurityIntegrationTest {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;
    @Autowired
    private JwtEncoder jwtEncoder;
    @Autowired
    private TraineeRepository traineeRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    void loginAndRegistrationArePublic() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"missing\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginAuthenticatesRawPasswordAgainstStoredBcryptHash() throws Exception {
        String username = "bcrypt." + UUID.randomUUID();
        String rawPassword = "CorrectHorseBatteryStaple";
        Trainee trainee = new Trainee();
        User user = new User();
        user.setFirstName("Bcrypt");
        user.setLastName("User");
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setActive(true);
        trainee.setUser(user);
        traineeRepository.saveAndFlush(trainee);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + rawPassword + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointRequiresValidBearerToken() throws Exception {
        mockMvc.perform(get("/trainings/training-types"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/trainings/training-types").cookie(new jakarta.servlet.http.Cookie("JSESSIONID", "legacy")))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/trainings/training-types").header("Authorization", "Bearer malformed"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/trainings/training-types").header("Authorization", "Bearer " + token()))
                .andExpect(status().isOk());
    }

    @Test
    void swaggerRemainsPublic() throws Exception {
        mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
    }

    @Test
    void allowsConfiguredCorsPreflight() throws Exception {
        mockMvc.perform(options("/trainings/training-types")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    void logoutRevokesOnlyTheCurrentToken() throws Exception {
        String loggedOutToken = token();
        String independentToken = token();

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/auth/logout").header("Authorization", "Bearer " + loggedOutToken))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/trainings/training-types").header("Authorization", "Bearer " + loggedOutToken))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/trainings/training-types").header("Authorization", "Bearer " + independentToken))
                .andExpect(status().isOk());
    }

    private String token() {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder().subject("Emily.Davis").claim("role", "TRAINEE")
                .issuedAt(now).expiresAt(now.plusSeconds(1200)).id(UUID.randomUUID().toString()).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
