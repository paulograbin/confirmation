package com.paulograbin.confirmation.web;

import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.exception.InvalidRequestException;
import com.paulograbin.confirmation.security.jwt.CurrentUser;
import com.paulograbin.confirmation.service.EventService;
import com.paulograbin.confirmation.service.ParticipationService;
import com.paulograbin.confirmation.service.UserService;
import com.paulograbin.confirmation.usecases.UpdateUserRequest;
import com.paulograbin.confirmation.web.dto.ChapterDTO;
import com.paulograbin.confirmation.web.dto.EventDetailsDTO;
import com.paulograbin.confirmation.web.dto.ParticipationWithoutUserDTO;
import com.paulograbin.confirmation.web.dto.UserDTO;
import com.paulograbin.confirmation.web.dto.UserDetailsDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;


@CrossOrigin("*")
@RestController
@RequestMapping(value = "/users")
public class UsersController {

    private static final Logger log = LoggerFactory.getLogger(UsersController.class);

    @Resource
    private ParticipationService participationService;

    @Resource
    private EventService eventService;

    @Resource
    private UserService userService;

    @Resource
    private ModelMapper modelMapper;


    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO getCurrentUser(@CurrentUser User currentUser) {
        Long currentUserId = currentUser.getId();
        log.info("Fetching /me for user {}", currentUserId);

        User userFromDatabase = userService.fetchById(currentUserId);

        List<ParticipationWithoutUserDTO> participations = participationService.getAllUpcomingParticipationsFromUser(currentUserId).stream()
                .map(p -> modelMapper.map(p, ParticipationWithoutUserDTO.class))
                .collect(Collectors.toList());

        List<EventDetailsDTO> createdEvents = eventService.fetchAllUpcomingEventsCreatedByUser(currentUserId).stream()
                .map(e -> modelMapper.map(e, EventDetailsDTO.class))
                .collect(Collectors.toList());

        UserDTO userDTO = modelMapper.map(userFromDatabase, UserDTO.class);
        userDTO.setParticipations(participations);
        userDTO.setCreatedEvents(createdEvents);

        return userDTO;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> listAll() {
        log.info("Listing all user");

        Iterable<User> users = userService.fetchAll();
        List<User> arrayList = Lists.from(users.iterator());

        return arrayList.stream()
                .map(u -> modelMapper.map(u, UserDTO.class))
                .collect(Collectors.toList());
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
    public UserDTO updateUser(@PathVariable Long id, @RequestBody @Valid UpdateUserRequest newUserInformation, @CurrentUser User currentUser) {
        if (id.longValue() != newUserInformation.getId().longValue()) {
            throw new InvalidRequestException("Request path doesn't match updated user");
        }

        User user = userService.updateUser(id, newUserInformation);
        return modelMapper.map(user, UserDTO.class);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO inactivateUser(@PathVariable Long id) {
        log.info(format("Inactivating user %d", id));
        User inactivatedUser = userService.inactivate(id);

        // todo lock this endpoint

        return modelMapper.map(inactivatedUser, UserDTO.class);
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
