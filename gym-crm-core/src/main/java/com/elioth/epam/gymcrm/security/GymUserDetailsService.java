package com.elioth.epam.gymcrm.security;

import com.elioth.epam.gymcrm.domain.Trainee;
import com.elioth.epam.gymcrm.domain.Trainer;
import com.elioth.epam.gymcrm.domain.User;
import com.elioth.epam.gymcrm.repository.TraineeRepository;
import com.elioth.epam.gymcrm.repository.TrainerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Loads a principal from the profile that owns the requested username. */
@Service
public class GymUserDetailsService implements UserDetailsService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public GymUserDetailsService(TraineeRepository traineeRepository, TrainerRepository trainerRepository) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return traineeRepository.findByUserUsername(username)
                .<UserDetails>map(this::traineePrincipal)
                .or(() -> trainerRepository.findByUserUsername(username).map(this::trainerPrincipal))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private GymUserPrincipal traineePrincipal(Trainee trainee) {
        return principal(trainee.getTraineeId(), trainee.getUser(), "ROLE_TRAINEE");
    }

    private GymUserPrincipal trainerPrincipal(Trainer trainer) {
        return principal(trainer.getTrainerId(), trainer.getUser(), "ROLE_TRAINER");
    }

    private GymUserPrincipal principal(Long profileId, User user, String role) {
        return new GymUserPrincipal(profileId, user.getUsername(), user.getPassword(),
                Boolean.TRUE.equals(user.getActive()), role);
    }
}
