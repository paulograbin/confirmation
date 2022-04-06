package com.paulograbin.confirmation.userequest;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRequestRepository extends CrudRepository<UserRequest, Long> {

    boolean existsByEmail(String email);

    Optional<UserRequest> findByCode(UUID code);

    Optional<UserRequest> findByEmail(String email);

    long countAllByUserNotNull();

}
