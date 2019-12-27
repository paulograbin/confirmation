package com.paulograbin.confirmation.persistence;

import com.paulograbin.confirmation.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UserDetails;


public interface UserRepository extends CrudRepository<User, Long> {

    boolean existsByUsername(String username);

    UserDetails findByUsername(String username);
}
