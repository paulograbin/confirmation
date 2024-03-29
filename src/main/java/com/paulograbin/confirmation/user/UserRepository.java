package com.paulograbin.confirmation.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends CrudRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    UserDetails findByUsername(String username);

    Optional<User> findByUsernameOrEmail(String username, String email);

    @EntityGraph(value = "graph.user.only")
    Optional<User> findById(Long userId);

    List<User> findAllByChapterId(Long chapterId);

    List<User> findAllByChapterIdAndActiveTrue(Long chapterId);

    long countAllByActiveTrue();

    long countAllByLastLoginNotNull();

}
