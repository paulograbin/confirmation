package com.paulograbin.confirmation.persistence;

import com.paulograbin.confirmation.domain.UserRequest;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRequestRepository extends CrudRepository<UserRequest, UUID> {

    boolean existsByEmail(String email);

}
