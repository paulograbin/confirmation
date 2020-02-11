package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.Role;
import com.paulograbin.confirmation.RoleName;
import com.paulograbin.confirmation.User;
import com.paulograbin.confirmation.exception.EmailNotAvailableException;
import com.paulograbin.confirmation.exception.NotFoundException;
import com.paulograbin.confirmation.exception.UsernameNotAvailableException;
import com.paulograbin.confirmation.persistence.RoleRepository;
import com.paulograbin.confirmation.persistence.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static java.lang.String.format;


@Service
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserRepository userRepository;

    @Resource
    private RoleRepository roleRepository;

    @Resource
    private PasswordEncoder passwordEncoder;


    public Iterable<User> fetchAll() {
        return userRepository.findAll();
    }

    public User fetchById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found exception"));
    }

    public User createUser(User user) {
        validateInformation(user);
        validateAvailableUsername(user.getUsername());
        validateAvailableEmail(user.getEmail());

        user.setId(null);
        user.setModificationDate(null);
        user.setInactivatedIn(null);
        user.setActive(true);
        user.setCreationDate(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new NotFoundException("Role not found"));
        user.setRoles(Collections.singleton(userRole));

        return userRepository.save(user);
    }

    private void validateAvailableEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailNotAvailableException(format("Email %s is already taken", email));
        }
    }

    private void validateAvailableUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameNotAvailableException(format("Username %s is already taken", username));
        }
    }

    private void validateInformation(User user) {

    }

    public User updateUser(Long id, User user) {
        //        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userFromDatabase = fetchById(id);

        userFromDatabase.setFirstName(user.getFirstName());
        userFromDatabase.setLastName(user.getLastName());
        userFromDatabase.setModificationDate(LocalDateTime.now());

        User updatedUser = userRepository.save(userFromDatabase);

        return updatedUser;
    }

    public User inactivate(Long id) {
        User userFromDatabase = fetchById(id);

        userFromDatabase.setActive(false);
        userFromDatabase.setInactivatedIn(LocalDateTime.now());
        userFromDatabase.setModificationDate(LocalDateTime.now());

        return userRepository.save(userFromDatabase);
    }

    public User activate(Long id) {
        User userFromDatabase = fetchById(id);

        userFromDatabase.setActive(true);
        userFromDatabase.setInactivatedIn(null);
        userFromDatabase.setModificationDate(LocalDateTime.now());

        return userRepository.save(userFromDatabase);
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));
    }

    public UserDetails loadUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }
}
