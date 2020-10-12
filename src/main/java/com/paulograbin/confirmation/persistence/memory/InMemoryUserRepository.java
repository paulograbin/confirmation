package com.paulograbin.confirmation.persistence.memory;

import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.persistence.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryUserRepository extends InMemoryRepository<User> implements UserRepository {

    @Override
    public boolean existsByUsername(String username) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean existsByEmail(String email) {
        return this.map.values()
                .stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
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
        return this.map.values()
                .stream()
                .filter(u -> u.getChapter().getId().equals(chapterId))
                .collect(Collectors.toList());
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
