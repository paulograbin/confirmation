package com.paulograbin.confirmation.persistence;

import com.paulograbin.confirmation.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface UserRepository extends CrudRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    UserDetails findByUsername(String username);

    Optional<User> findByUsernameOrEmail(String username, String email);

    List<User> findAllByChapterId(Long chapterId);

    long countAllByActiveTrue();

    long countAllByLastLoginNotNull();

}
