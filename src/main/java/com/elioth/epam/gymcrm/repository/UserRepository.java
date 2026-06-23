package com.elioth.epam.gymcrm.repository;

import com.elioth.epam.gymcrm.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
