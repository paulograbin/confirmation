package com.paulograbin.confirmation.persistence.memory;

import com.paulograbin.confirmation.userequest.UserRequest;
import com.paulograbin.confirmation.userequest.UserRequestRepository;

import java.util.Optional;
import java.util.UUID;

public class InMemoryUserRequestRepository extends InMemoryRepository<UserRequest> implements UserRequestRepository {

    @Override
    public boolean existsByEmail(String email) {
        return map.values()
                .stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public Optional<UserRequest> findByCode(UUID code) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long countAllByUserNotNull() {
        throw new UnsupportedOperationException();
    }
}
