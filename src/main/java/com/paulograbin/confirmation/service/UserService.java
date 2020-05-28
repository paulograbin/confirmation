package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.domain.*;
import com.paulograbin.confirmation.exception.EmailNotAvailableException;
import com.paulograbin.confirmation.exception.NotFoundException;
import com.paulograbin.confirmation.exception.UserAlreadyAssignedToChapterException;
import com.paulograbin.confirmation.exception.UsernameNotAvailableException;
import com.paulograbin.confirmation.persistence.RoleRepository;
import com.paulograbin.confirmation.persistence.UserRepository;
import com.paulograbin.confirmation.security.jwt.resource.SignUpRequest;
import com.paulograbin.confirmation.usecases.UpdateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;


@Service
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserRepository userRepository;

    @Resource
    private RoleRepository roleRepository;

    @Resource
    private EventService eventService;

    @Resource
    private ChapterService chapterService;

    @Resource
    private PasswordEncoder passwordEncoder;


    public Iterable<User> fetchAll() {
        return userRepository.findAll();
    }

    public User fetchById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User " + id + " not found!"));
    }

    public User createUser(User userToCreate) {
        validateInformation(userToCreate);
        validateAvailableUsername(userToCreate.getUsername());
        validateAvailableEmail(userToCreate.getEmail());

        userToCreate.setId(null);
        userToCreate.setPassword(passwordEncoder.encode(userToCreate.getPassword()));
        userToCreate.setCreationDate(LocalDateTime.now());

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new NotFoundException("Role not found"));
        userToCreate.setRoles(Collections.singleton(userRole));

        return userRepository.save(userToCreate);
    }

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

    public User updateUser(Long id, UpdateUserRequest updateRequest) {
        User userFromDatabase = fetchById(id);

        userFromDatabase.setFirstName(updateRequest.getFirstName());
        userFromDatabase.setLastName(updateRequest.getLastName());
        userFromDatabase.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        userFromDatabase.setModificationDate(LocalDateTime.now());

        return userRepository.save(userFromDatabase);
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

    public User fetchByUsername(final String username) {
        return (User) this.loadUserByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail.toLowerCase(), usernameOrEmail.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));
    }

    public UserDetails loadUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

    public User setAsMaster(final long userId) {
        log.info("Setting user {} as master", userId);
        User userFromDatabase = fetchById(userId);

        userFromDatabase.setMaster(true);

        return userRepository.save(userFromDatabase);
    }

    public User grantRoles(final long userId, Set<Role> rolesToAdd) {
        final User userFromDatabase = fetchById(userId);

        Set<Role> roles = userFromDatabase.getRoles();
        roles.addAll(rolesToAdd);

        userFromDatabase.setRoles(roles);

        return userRepository.save(userFromDatabase);
    }

    @Transactional
    public User assignUserToChapter(Long userId, long chapterId) {
        final User userFromDatabase = this.fetchById(userId);
        final Chapter chapterFromDatabase = chapterService.fetchById(chapterId);

        if (userFromDatabase.getChapters().contains(chapterFromDatabase)) {
            throw new UserAlreadyAssignedToChapterException(format("User %s already belongs to %s", userId, chapterId));
        }

        userFromDatabase.addChapter(chapterFromDatabase);
        final User savedUser = userRepository.save(userFromDatabase);

        List<Event> upcomingEvents = eventService.fetchUpComingEventsFromChapter(chapterId);
        for (Event upcomingEvent : upcomingEvents) {
            try {
                eventService.inviteUserForEvent(userId, upcomingEvent.getId());
            } catch (RuntimeException e) {
                log.info("Something went wrong while inviting user {} to event {}: {}", userFromDatabase.getId(),
                        upcomingEvent.getId(),
                        e.getMessage());
            }
        }

        return savedUser;
    }

    public List<User> fetchAllByChapterId(Long chapterId) {
        return userRepository.findAllByChapterId(chapterId);
    }

    public long fetchCount() {
        return userRepository.count();
    }
}
