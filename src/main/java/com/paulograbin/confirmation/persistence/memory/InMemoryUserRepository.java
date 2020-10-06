package com.paulograbin.confirmation.persistence.memory;

import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.persistence.UserRepository;
import com.paulograbin.confirmation.userequest.UserRequest;
import com.paulograbin.confirmation.userequest.UserRequestRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryUserRepository extends InMemoryRepository<User> implements UserRepository {

    @Override
    public boolean existsByUsername(String username) {
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public UserDetails findByUsername(String username) {
        return null;
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String username, String email) {
        return Optional.empty();
    }

    @Override
    public List<User> findAllByChapterId(Long chapterId) {
        return null;
    }

    @Override
    public long countAllByActiveTrue() {
        return 0;
    }

    @Override
    public long countAllByLastLoginNotNull() {
        return 0;
    }
}
