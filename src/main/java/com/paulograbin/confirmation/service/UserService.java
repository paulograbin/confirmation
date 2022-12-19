package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.DateUtils;
import com.paulograbin.confirmation.chapter.Chapter;
import com.paulograbin.confirmation.chapter.ChapterService;
import com.paulograbin.confirmation.domain.Role;
import com.paulograbin.confirmation.event.EventService;
import com.paulograbin.confirmation.exception.EmailNotAvailableException;
import com.paulograbin.confirmation.exception.InvalidRequestException;
import com.paulograbin.confirmation.exception.NotFoundException;
import com.paulograbin.confirmation.exception.UsernameNotAvailableException;
import com.paulograbin.confirmation.participation.ParticipationRepository;
import com.paulograbin.confirmation.service.mail.EmailService;
import com.paulograbin.confirmation.usecases.user.UpdateUserRequest;
import com.paulograbin.confirmation.usecases.user.UpdateUserRequestAdmin;
import com.paulograbin.confirmation.usecases.user.harddelete.UserHardDeleteRequest;
import com.paulograbin.confirmation.usecases.user.harddelete.UserHardDeleteResponse;
import com.paulograbin.confirmation.usecases.user.harddelete.UserHardDeleteUseCase;
import com.paulograbin.confirmation.user.User;
import com.paulograbin.confirmation.user.UserRepository;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;


@Service
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserRepository userRepository;

    @Resource
    private ParticipationRepository participationRepository;

    @Resource
    private RoleService roleService;

    @Resource
    private EventService eventService;

    @Resource
    private ChapterService chapterService;

    @Resource
    private EmailService emailService;

    @Resource
    private PasswordEncoder passwordEncoder;


    public Iterable<User> fetchAll(User currentUser) {
        if (!isAdmin(currentUser)) {
            return Collections.emptyList();
        }

        return fetchAll();
    }

    private Iterable<User> fetchAll() {
        return userRepository.findAll();
    }

    public boolean isAdmin(User currentUser) {
        log.info("Checking if user {} is admin", currentUser.getId());

        final Role adminRole = roleService.getAdminRole();

        return currentUser.getRoles().stream()
//                .map(Role::getName)
                .anyMatch(p -> p.equals(adminRole));
    }

    public User fetchById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User " + id + " not found!"));
    }

    public User fetchBasicInformationAboutUser(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User " + id + " not found!"));
    }

    public User createUser(User userToCreate) {
        validateInformation(userToCreate);
        validateAvailableUsername(userToCreate.getUsername());
        validateAvailableEmail(userToCreate.getEmail());

        userToCreate.setId(null);
        userToCreate.setPassword(passwordEncoder.encode(userToCreate.getPassword()));
        userToCreate.setCreationDate(DateUtils.getCurrentDate());

        assignUserRole(userToCreate);

        return userRepository.save(userToCreate);
    }

    private void assignUserRole(User userToCreate) {
        Role userRole = roleService.getUserRole();

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        userToCreate.setRoles(roles);
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
        if (isBlank(user.getEmail())) {
            throw new IllegalArgumentException("Email do usuário não pode ser nulo");
        }

        if (isBlank(user.getUsername())) {
            throw new IllegalArgumentException("Username do usuário não pode ser nulo");
        }


        if (isBlank(user.getPassword())) {
            throw new IllegalArgumentException("Senha do usuário não pode ser nulo");
        }
    }


    public User updateUser(Long id, UpdateUserRequest updateRequest) {
//        UpdateUserResponse response = new UpdateUserResponse();
//        new UpdateUserUseCase(userRepository, updateRequest).execute();

        User userFromDatabase = fetchById(id);

        if (!id.equals(updateRequest.getId())) {
            throw new InvalidRequestException("Provided id and request don't match");
        }

        if (updateRequest.getPassword().length() < 6 || updateRequest.getPassword().length() > 128) {
            throw new InvalidRequestException("Password doesn't match required size");
        }

        userFromDatabase.setFirstName(updateRequest.getFirstName());
        userFromDatabase.setLastName(updateRequest.getLastName());
        userFromDatabase.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        userFromDatabase.setModificationDate(DateUtils.getCurrentDate());

        emailService.sendPasswordChangedMail(userFromDatabase);

        return userRepository.save(userFromDatabase);
    }

    public User updateUserForAdmin(Long id, UpdateUserRequestAdmin updateRequest) {
        User userFromDatabase = fetchById(id);
        log.info("Updating user {}", userFromDatabase.getUsername());

        if (!id.equals(updateRequest.getId())) {
            throw new InvalidRequestException("Provided id and request don't match");
        }

        if (!userFromDatabase.getEmail().equalsIgnoreCase(updateRequest.getEmail())) {
            validateAvailableEmail(updateRequest.getEmail());
            userFromDatabase.setEmail(updateRequest.getEmail());
        }

        if (!userFromDatabase.getUsername().equalsIgnoreCase(updateRequest.getUsername())) {
            validateAvailableUsername(updateRequest.getUsername());
            userFromDatabase.setUsername(updateRequest.getUsername());
        }

        userFromDatabase.setFirstName(updateRequest.getFirstName());
        userFromDatabase.setLastName(updateRequest.getLastName());
        userFromDatabase.setMaster(updateRequest.getMaster());
        userFromDatabase.setModificationDate(DateUtils.getCurrentDate());

        if (userFromDatabase.getChapter().getId() != updateRequest.getChapter()) {
            Chapter chapterToAssignToUser = chapterService.fetchById(updateRequest.getChapter());

            userFromDatabase.setChapter(chapterToAssignToUser);
        }

        if (!updateRequest.getActive()) {
            inactivate(userFromDatabase.getId());
        } else {
            activate(userFromDatabase.getId());
        }

//        userFromDatabase.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
//        emailService.sendPasswordChangedMail(userFromDatabase);

        return userRepository.save(userFromDatabase);
    }

    public User inactivate(Long id) {
        User userFromDatabase = fetchById(id);
        log.info("Inactivating user {}", userFromDatabase.getUsername());

        userFromDatabase.setActive(false);
        userFromDatabase.setInactivatedIn(DateUtils.getCurrentDate());
        userFromDatabase.setModificationDate(DateUtils.getCurrentDate());

        return userRepository.save(userFromDatabase);
    }

    public UserHardDeleteResponse hardDelete(UserHardDeleteRequest request, User currentUser) {
        UserHardDeleteResponse response = new UserHardDeleteResponse();
        request.setRequestingUser(currentUser.getId());

        new UserHardDeleteUseCase(request, response, userRepository, participationRepository).execute();

        return response;
    }

    public User activate(Long id) {
        User userFromDatabase = fetchById(id);
        log.info("Activating user {}", userFromDatabase.getUsername());

        userFromDatabase.setActive(true);
        userFromDatabase.setInactivatedIn(null);
        userFromDatabase.setModificationDate(DateUtils.getCurrentDate());

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


    public User setAsMaster(final long userId) {
        log.info("Setting user {} as master", userId);
        User userFromDatabase = fetchById(userId);

        userFromDatabase.setMaster(true);

        return userRepository.save(userFromDatabase);
    }

    @Transactional
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

//        if (userFromDatabase.getChapter().getId().equals(chapterFromDatabase.getId())) {
//            throw new UserAlreadyAssignedToChapterException(format("User %s already belongs to %s", userId, chapterId));
//        }

//        var chapters = userFromDatabase.getChapters();
//        chapters.add(chapterFromDatabase);
//        userFromDatabase.setChapters(new ArrayList<>(chapters));

        userFromDatabase.addToChapter(chapterFromDatabase);

        return userRepository.save(userFromDatabase);
    }

    public long fetchCount() {
        return userRepository.count();
    }

    public void updateLoginDate(UserDetails userDetails) {
        User user = (User) userDetails;
        user.setLastLogin(DateUtils.getCurrentDate());

        userRepository.save(user);
    }
}
