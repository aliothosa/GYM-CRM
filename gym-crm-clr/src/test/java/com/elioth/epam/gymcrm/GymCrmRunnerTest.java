package com.elioth.epam.gymcrm;

import com.elioth.epam.gymcrm.auth.AuthSession;
import com.elioth.epam.gymcrm.auth.Role;
import com.elioth.epam.gymcrm.facade.GymCrmFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymCrmRunnerTest {

    @Mock
    private GymCrmFacade facade;

    private InputStream originalIn;
    private PrintStream originalOut;
    private ByteArrayOutputStream capturedOut;

    @BeforeEach
    void setUpStreams() {
        originalIn = System.in;
        originalOut = System.out;
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    void shouldShowPublicMenuOnStartup() {
        GymCrmRunner runner = createRunnerWithInput("5\n");

        runner.run();

        String output = capturedOutput();
        assertTrue(output.contains("GYM CRM DEMO APP"));
        assertTrue(output.contains("MAIN MENU"));
        assertTrue(output.contains("Goodbye!"));
    }

    @Test
    void shouldLoginAsTraineeAndOpenTraineeMenu() {
        AuthSession session = new AuthSession(1L, "Emily.Davis", Role.TRAINEE);
        when(facade.loginAsTrainee("Emily.Davis", "pass123")).thenReturn(session);
        when(facade.getCurrentSession()).thenReturn(session);

        GymCrmRunner runner = createRunnerWithInput(
                "1\nEmily.Davis\npass123\n7\n5\n"
        );

        runner.run();

        verify(facade).loginAsTrainee("Emily.Davis", "pass123");
        assertTrue(capturedOutput().contains("TRAINEE MENU"));
        assertTrue(capturedOutput().contains("Logged out."));
    }

    @Test
    void shouldLoginAsTrainerAndOpenTrainerMenu() {
        AuthSession session = new AuthSession(2L, "John.Doe", Role.TRAINER);
        when(facade.loginAsTrainer("John.Doe", "pass123")).thenReturn(session);
        when(facade.getCurrentSession()).thenReturn(session);

        GymCrmRunner runner = createRunnerWithInput(
                "2\nJohn.Doe\npass123\n6\n5\n"
        );

        runner.run();

        verify(facade).loginAsTrainer("John.Doe", "pass123");
        assertTrue(capturedOutput().contains("TRAINER MENU"));
    }

    @Test
    void shouldHandleInvalidMenuOption() {
        GymCrmRunner runner = createRunnerWithInput("99\n5\n");

        runner.run();

        assertTrue(capturedOutput().contains("Invalid option. Please try again."));
    }

    @Test
    void shouldHandleBusinessExceptionWithoutCrashing() {
        doThrow(new RuntimeException("Invalid credentials"))
                .when(facade).loginAsTrainee("bad.user", "wrong");

        GymCrmRunner runner = createRunnerWithInput(
                "1\nbad.user\nwrong\n5\n"
        );

        runner.run();

        String output = capturedOutput();
        assertTrue(output.contains("Login failed: Invalid credentials"));
        assertTrue(output.contains("MAIN MENU"));
    }

    @Test
    void shouldLogoutAndReturnToPublicMenu() {
        AuthSession session = new AuthSession(1L, "Emily.Davis", Role.TRAINEE);
        when(facade.loginAsTrainee("Emily.Davis", "pass123")).thenReturn(session);
        when(facade.getCurrentSession()).thenReturn(session);

        GymCrmRunner runner = createRunnerWithInput(
                "1\nEmily.Davis\npass123\n7\n5\n"
        );

        runner.run();

        verify(facade).logout();
        assertTrue(capturedOutput().contains("MAIN MENU"));
    }

    @Test
    void shouldSkipRunWhenCliDisabled() {
        System.setIn(new ByteArrayInputStream("5\n".getBytes(StandardCharsets.UTF_8)));
        GymCrmRunner runner = new GymCrmRunner(facade);
        ReflectionTestUtils.setField(runner, "cliEnabled", false);

        runner.run();

        assertTrue(capturedOutput().isEmpty());
    }

    private GymCrmRunner createRunnerWithInput(String scriptedInput) {
        System.setIn(new ByteArrayInputStream(scriptedInput.getBytes(StandardCharsets.UTF_8)));
        GymCrmRunner runner = new GymCrmRunner(facade);
        ReflectionTestUtils.setField(runner, "cliEnabled", true);
        return runner;
    }

    private String capturedOutput() {
        return capturedOut.toString(StandardCharsets.UTF_8);
    }
}
