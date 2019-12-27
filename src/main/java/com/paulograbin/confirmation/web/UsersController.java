package com.paulograbin.confirmation.web;

import com.paulograbin.confirmation.User;
import com.paulograbin.confirmation.service.UserService;
import com.paulograbin.confirmation.web.dto.UserDTO;
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
import java.util.stream.Stream;


@CrossOrigin("*")
@RestController
@RequestMapping(value = "/users")
public class UsersController {

    private static final Logger log = LoggerFactory.getLogger(UsersController.class);

    @Resource
    private UserService userService;

    private ModelMapper modelMapper = new ModelMapper();


    @RequestMapping(method = RequestMethod.GET)
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
    public User createNewUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public UserDTO listUser(@PathVariable long id) {
        User user = userService.fetchById(id);

        return modelMapper.map(user, UserDTO.class);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public User inactivateUser(@PathVariable Long id) {
        return userService.inactivate(id);
    }
}
