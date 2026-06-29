package com.elioth.epam.gymcrm;

import com.elioth.epam.gymcrm.auth.AuthSession;
import com.elioth.epam.gymcrm.auth.Role;
import com.elioth.epam.gymcrm.facade.GymCrmFacade;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Interactive CLI testing is hard while GymCrmRunner uses Scanner(System.in) directly.
 * TODO: refactor runner to use InputReader / ConsolePrinter abstractions for testability.
 */
@ExtendWith(MockitoExtension.class)
@Disabled("Practice skeleton - implement after InputReader refactor or use scripted InputStream")
class GymCrmRunnerTest {

    @Mock
    private GymCrmFacade facade;

    @Test
    @Disabled("Practice skeleton")
    void shouldShowPublicMenuOnStartup() {
        // Arrange
        // TODO: inject facade into GymCrmRunner with cliEnabled=true
        // TODO: provide ByteArrayInputStream with "5\n" (exit) as System.in substitute after refactor

        // Act
        // TODO: run runner

        // Assert
        // TODO: verify banner "GYM CRM DEMO APP" printed
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldLoginAsTraineeAndOpenTraineeMenu() {
        // Arrange
        // TODO: script input: "1\n" (login trainee), username, password, then "7\n" (logout), "5\n" (exit)
        // TODO: mock facade.loginAsTrainee(...) -> AuthSession

        // Assert
        // TODO: verify facade.loginAsTrainee called once
        // TODO: verify trainee menu actions available
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldLoginAsTrainerAndOpenTrainerMenu() {
        // Arrange
        // TODO: script input for trainer login path

        // Assert
        // TODO: verify facade.loginAsTrainer called
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldHandleInvalidMenuOption() {
        // Arrange
        // TODO: input invalid option then exit

        // Assert
        // TODO: verify "Invalid option" message without exception
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldHandleBusinessExceptionWithoutCrashing() {
        // Arrange
        // TODO: mock facade.loginAsTrainee to throw RuntimeException

        // Act
        // TODO: run login flow

        // Assert
        // TODO: verify error message printed and public menu shown again
    }

    @Test
    @Disabled("Practice skeleton")
    void shouldLogoutAndReturnToPublicMenu() {
        // Arrange
        // TODO: login then choose logout option

        // Assert
        // TODO: verify facade.logout() called
        // TODO: verify main menu shown again
    }
}
