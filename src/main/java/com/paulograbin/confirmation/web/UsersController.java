package com.paulograbin.confirmation.web;

import com.paulograbin.confirmation.User;
import com.paulograbin.confirmation.security.jwt.CurrentUser;
import com.paulograbin.confirmation.service.ParticipationService;
import com.paulograbin.confirmation.service.UserService;
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
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;


@CrossOrigin("*")
@RestController
@RequestMapping(value = "/users")
public class UsersController {

    private static final Logger log = LoggerFactory.getLogger(UsersController.class);

    @Resource
    private UserService userService;

    @Resource
    private ParticipationService participationService;

    @Resource
    private ModelMapper modelMapper;


    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDetailsDTO getCurrentUser(@CurrentUser User currentUser) {
        UserDetailsDTO user = modelMapper.map(currentUser, UserDetailsDTO.class);

        return user;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> listAll() {
        Iterable<User> users = userService.fetchAll();
        List<User> arrayList = Lists.from(users.iterator());

        return arrayList.stream()
                .map(u -> modelMapper.map(u, UserDTO.class))
                .collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createNewUser(@RequestBody User userToBeCreated) {
        User createdUser = userService.createUser(userToBeCreated);

        // todo stop user from creating as master

        return modelMapper.map(createdUser, UserDTO.class);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public UserDTO listUser(@PathVariable long id) {
        User user = userService.fetchById(id);

        return modelMapper.map(user, UserDTO.class);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public UserDTO updateUser(@PathVariable Long id, @RequestBody User newUserInformation) {
        User user = userService.updateUser(id, newUserInformation);
        return modelMapper.map(user, UserDTO.class);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public UserDTO inactivateUser(@PathVariable Long id) {
        log.info(format("Inactivating user %d", id));
        User inactivatedUser = userService.inactivate(id);

        // todo lock this endpoint

        return modelMapper.map(inactivatedUser, UserDTO.class);
    }

    @RequestMapping(path = "/{id}/activate", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public UserDTO activateUser(@PathVariable Long id) {
        log.info(format("Activating user %d", id));
        User activatedUser = userService.activate(id);

        return modelMapper.map(activatedUser, UserDTO.class);
    }

    @RequestMapping(path = "/{id}/participations", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getAllParticipation(@PathVariable Long id) {
        log.info(format("Fetching all participations from user %d", id));
        User activatedUser = userService.activate(id);

//        participationService

        return modelMapper.map(activatedUser, UserDTO.class);
    }
}
