package com.elioth.epam.gymcrm.security;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.User;
import com.elioth.epam.gymcrm.repository.TraineeRepository;
import com.elioth.epam.gymcrm.repository.TrainerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymUserDetailsServiceTest {

    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;

    @Test
    void loadsTraineeWithTraineeAuthority() {
        when(traineeRepository.findByUserUsername("trainee"))
                .thenReturn(Optional.of(trainee("trainee", true)));

        GymUserPrincipal principal = (GymUserPrincipal) service().loadUserByUsername("trainee");

        assertEquals("ROLE_TRAINEE", principal.getAuthorities().iterator().next().getAuthority());
        assertTrue(principal.isEnabled());
    }

    @Test
    void loadsTrainerWithTrainerAuthorityAndInactiveStatus() {
        when(traineeRepository.findByUserUsername("trainer")).thenReturn(Optional.empty());
        when(trainerRepository.findByUserUsername("trainer"))
                .thenReturn(Optional.of(trainer("trainer", false)));

        GymUserPrincipal principal = (GymUserPrincipal) service().loadUserByUsername("trainer");

        assertEquals("ROLE_TRAINER", principal.getAuthorities().iterator().next().getAuthority());
        assertFalse(principal.isEnabled());
    }

    @Test
    void rejectsUnknownUsername() {
        when(traineeRepository.findByUserUsername("missing")).thenReturn(Optional.empty());
        when(trainerRepository.findByUserUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service().loadUserByUsername("missing"));
    }

    private GymUserDetailsService service() {
        return new GymUserDetailsService(traineeRepository, trainerRepository);
    }

    private Trainee trainee(String username, boolean active) {
        Trainee trainee = new Trainee();
        trainee.setTraineeId(1L);
        trainee.setUser(user(username, active));
        return trainee;
    }

    private Trainer trainer(String username, boolean active) {
        Trainer trainer = new Trainer();
        trainer.setTrainerId(2L);
        trainer.setUser(user(username, active));
        return trainer;
    }

    private User user(String username, boolean active) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("{bcrypt}encodedPassword");
        user.setActive(active);
        return user;
    }
}
