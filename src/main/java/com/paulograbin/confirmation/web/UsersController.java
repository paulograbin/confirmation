package com.paulograbin.confirmation.web;

import com.google.gson.Gson;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.exception.InvalidRequestException;
import com.paulograbin.confirmation.participation.ParticipationService;
import com.paulograbin.confirmation.security.jwt.CurrentUser;
import com.paulograbin.confirmation.service.EventService;
import com.paulograbin.confirmation.service.UserService;
import com.paulograbin.confirmation.usecases.user.UpdateUserRequest;
import com.paulograbin.confirmation.usecases.user.UpdateUserRequestAdmin;
import com.paulograbin.confirmation.usecases.user.harddelete.UserHardDeleteRequest;
import com.paulograbin.confirmation.usecases.user.harddelete.UserHardDeleteResponse;
import com.paulograbin.confirmation.web.dto.UserDTO;
import com.paulograbin.confirmation.web.dto.UserDetailsDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;


@CrossOrigin("*")
@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
class UsersController {

    private static final Logger log = LoggerFactory.getLogger(UsersController.class);

    @Resource
    private UserService userService;

    @Resource
    private ModelMapper modelMapper;


    @GetMapping(value = "/me")
    public ResponseEntity<UserDTO> getCurrentUser(@CurrentUser User currentUser) {
        Long currentUserId = currentUser.getId();
        log.info("Fetching /me for user {}", currentUserId);

        User userFromDatabase = userService.fetchById(currentUserId);
        UserDTO userDTO = modelMapper.map(userFromDatabase, UserDTO.class);
        userDTO.setParticipations(Collections.emptyList());

        CacheControl cc = CacheControl.maxAge(Duration.ofHours(1)).cachePrivate();

        return ResponseEntity.ok()
                .cacheControl(cc)
                .body(userDTO);
    }

    @GetMapping(value = "/panel")
    @Cacheable(value = "admin", key = "#currentUser.id")
    public boolean canOpenAdminPanel(@CurrentUser User currentUser) {
        log.info("Fetching /panel for user {}", currentUser.getId());

        return userService.isAdmin(currentUser);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<UserDTO>> listAll(@CurrentUser User currentUser) {
        log.info("Listing all users");

        Iterable<User> users = userService.fetchAll(currentUser);
        List<User> arrayList = Lists.from(users.iterator());

        List<UserDTO> collect = arrayList.stream()
                .map(u -> modelMapper.map(u, UserDTO.class))
                .collect(Collectors.toList());

        CacheControl cc = CacheControl.maxAge(Duration.ofMinutes(10)).cachePrivate();
        return ResponseEntity.ok()
                .cacheControl(cc)
                .body(collect);
    }

//    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseStatus(HttpStatus.CREATED)
//    public UserDTO createNewUser(@RequestBody SignUpRequest userToBeCreated) {
//        log.info("Creating new user {}", userToBeCreated.getUsername());
//
//        User createdUser = userService.createUser(userToBeCreated);
//
//        return modelMapper.map(createdUser, UserDTO.class);
//    }

    @GetMapping(path = "/{id}/participations")
    @ResponseStatus(HttpStatus.OK)
    public UserDetailsDTO getAllParticipation(@PathVariable Long id) {
        log.info(format("Fetching all participations from user %d", id));
        User user = userService.fetchById(id);

        return modelMapper.map(user, UserDetailsDTO.class);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO listUser(@PathVariable long id) {
        log.info(format("Fetching user %d", id));

        User user = userService.fetchById(id);

        return modelMapper.map(user, UserDTO.class);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest newUserInformation, @CurrentUser User currentUser) {
        if (id.longValue() != newUserInformation.getId().longValue()) {
            throw new InvalidRequestException("Request path doesn't match updated user");
        }

        User user = userService.updateUser(id, newUserInformation);
        return modelMapper.map(user, UserDTO.class);
    }

    @PutMapping(path = "/{id}/admin")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO updateUserForAdmin(@PathVariable Long id, @RequestBody UpdateUserRequestAdmin updateUserRequest, @CurrentUser User currentUser) {
        log.info("New update user for admin request");

        if (!userService.isAdmin(currentUser)) {
            throw new RuntimeException("Not admin");
        }

        if (id.longValue() != updateUserRequest.getId().longValue()) {
            throw new InvalidRequestException("Request path doesn't match updated user");
        }

        User user = userService.updateUserForAdmin(id, updateUserRequest);
        return modelMapper.map(user, UserDTO.class);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO inactivateUser(@PathVariable Long id) {
        log.info("Inactivating user {}", id);
        User inactivatedUser = userService.inactivate(id);

        // todo lock this endpoint

        return modelMapper.map(inactivatedUser, UserDTO.class);
    }

    @DeleteMapping(path = "/{id}/harddelete")
    @ResponseStatus(HttpStatus.OK)
    public String hardDeleteUser(@PathVariable Long id, @CurrentUser User currentUser) {
        log.info("Hard deleting user {}", id);

        UserHardDeleteRequest request = new UserHardDeleteRequest();
        request.setUserToDeleteId(id);
        request.setRequestingUser(currentUser.getId());

        UserHardDeleteResponse response = userService.hardDelete(request, currentUser);

        return new Gson().toJson(response);
    }

    @PutMapping(path = "/{id}/activate")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO activateUser(@PathVariable Long id) {
        log.info(format("Activating user %d", id));
        User activatedUser = userService.activate(id);

        return modelMapper.map(activatedUser, UserDTO.class);
    }

    @PutMapping(path = "/{userId}/chapter/{chapterId}")
    @ResponseStatus(HttpStatus.OK)
    public void addToChapter(@PathVariable("userId") long userId, @PathVariable("chapterId") long chapterId) {
        log.info("Adding user {} to chapter {}", userId, chapterId);
        userService.assignUserToChapter(userId, chapterId);
    }
}
