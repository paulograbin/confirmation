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
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean existsByEmail(String email) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserDetails findByUsername(String username) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String username, String email) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<User> findAllByChapterId(Long chapterId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long countAllByActiveTrue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long countAllByLastLoginNotNull() {
        throw new UnsupportedOperationException();
    }
}
