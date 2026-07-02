package com.elioth.epam.gymcrm.repository;

import com.elioth.epam.gymcrm.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User persistUser(String firstName, String lastName, String username) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword("secret");
        user.setActive(true);
        return userRepository.save(user);
    }

    @Test
    void countByFirstNameAndLastName_shouldReturnCount_whenUsersExist() {
        persistUser("John", "Smith", "john.smith1");
        persistUser("John", "Smith", "john.smith2");
        persistUser("Jane", "Doe", "jane.doe");

        long count = userRepository.countByFirstNameAndLastName("John", "Smith");

        assertEquals(2, count);
    }

    @Test
    void countByFirstNameAndLastName_shouldReturnZero_whenNoUsersMatch() {
        persistUser("John", "Smith", "john.smith");

        long count = userRepository.countByFirstNameAndLastName("Jane", "Doe");

        assertEquals(0, count);
    }
}
