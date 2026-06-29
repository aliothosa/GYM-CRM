package com.elioth.epam.gymcrm.repository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
@Disabled("Requires H2 or dedicated test database configuration; enable after test profile is set up.")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void countByFirstNameAndLastName_shouldReturnCount_whenUsersExist() {
        // TODO: persist two Users with firstName "John" and lastName "Smith"; one unrelated user
        // TODO: call userRepository.countByFirstNameAndLastName("John", "Smith")
        // TODO: assert count equals 2
    }

    @Test
    void countByFirstNameAndLastName_shouldReturnZero_whenNoUsersMatch() {
        // TODO: persist users with other name combinations only (or empty database)
        // TODO: call userRepository.countByFirstNameAndLastName("Jane", "Doe")
        // TODO: assert count equals 0
    }
}
