package com.paulograbin.confirmation.passwordreset;

import com.paulograbin.confirmation.persistence.memory.InMemoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class InMemoryPasswordResetRepository extends InMemoryRepository<PasswordRequest> implements PasswordResetRepository {

    @Override
    public Optional<PasswordRequest> findByCode(UUID code) {
        return this.map.values().stream()
                .filter(p -> p.getCode().equals(code))
                .findFirst();
    }

    @Override
    public boolean existsByCode(UUID code) {
        return this.map.values().stream()
                .anyMatch(p -> p.getCode().equals(code));
    }

    @Override
    public List<PasswordRequest> findByEmailAddress(String emailAddress) {
        return this.map.values().stream()
                .filter(p -> p.getEmailAddress().equalsIgnoreCase(emailAddress))
                .collect(Collectors.toList());
    }
}
