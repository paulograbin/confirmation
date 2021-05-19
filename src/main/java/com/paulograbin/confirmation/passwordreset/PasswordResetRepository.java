package com.paulograbin.confirmation.passwordreset;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface PasswordResetRepository extends CrudRepository<PasswordRequest, Long> {

    Optional<PasswordRequest> findByCode(UUID code);

    List<PasswordRequest> findByEmailAddress(String emailAddress);

}
