package com.paulograbin.confirmation.web;

import com.google.gson.Gson;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.domain.UserRequest;
import com.paulograbin.confirmation.persistence.ChapterRepository;
import com.paulograbin.confirmation.persistence.UserRepository;
import com.paulograbin.confirmation.persistence.UserRequestRepository;
import com.paulograbin.confirmation.security.jwt.CurrentUser;
import com.paulograbin.confirmation.usecases.pseudouser.convertion.ConvertPseudoUserRequest;
import com.paulograbin.confirmation.usecases.pseudouser.creation.CreatePseudoUserRequest;
import com.paulograbin.confirmation.usecases.pseudouser.creation.CreatePseudoUserResponse;
import com.paulograbin.confirmation.usecases.pseudouser.creation.CreatePseudoUserUseCase;
import com.paulograbin.confirmation.usecases.pseudouser.read.ReadPseudoUserRequest;
import com.paulograbin.confirmation.usecases.pseudouser.read.ReadPseudoUserResponse;
import com.paulograbin.confirmation.usecases.pseudouser.read.ReadPseudoUserUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.net.URI;
import java.util.UUID;

import static com.paulograbin.confirmation.DateUtils.getCurrentDate;


@CrossOrigin("*")
@RestController
@RequestMapping(value = "/userrequest", produces = MediaType.APPLICATION_JSON_VALUE)
class UserRequestController {

    private static final Logger log = LoggerFactory.getLogger(UserRequestController.class);

    @Resource
    private UserRequestRepository repository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private ChapterRepository chapterRepository;

    @Resource
    private PasswordEncoder passwordEncoder;

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ReadPseudoUserResponse> listAll(@PathVariable UUID id) {
        log.info("Listing user request {}", id);

        ReadPseudoUserRequest readPseudoUserRequest = new ReadPseudoUserRequest();
        readPseudoUserRequest.requestId = id;

        ReadPseudoUserResponse response = new ReadPseudoUserUseCase(readPseudoUserRequest, repository).execute();

        if (response.successful) {
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User convertRequestToUser(@RequestBody ConvertPseudoUserRequest request) {
        log.info("Received pseudo user convertion request");

        UserRequest userRequest = repository.findById(request.requestNumber)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (userRequest.getUser() != null) { //todo or expired
            throw new RuntimeException("Request already used");
        }

        if (getCurrentDate().isAfter(userRequest.getExpirationDate())) {
            throw new RuntimeException("Request expired");
        }

        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setUsername(request.username);
        user.setFirstName(request.firstName);
        user.setLastName(request.lastName);
        user.setPassword(passwordEncoder.encode(request.password));

        user.setCreationDate(getCurrentDate());

        user.setChapter(userRequest.getChapter());
        userRequest.getChapter().getUsers().add(user);


        userRepository.save(user);

        userRequest.setConvertionDate(getCurrentDate());
        userRequest.setUser(user);
        repository.save(userRequest);

        return user;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createUserRequest(@RequestBody CreatePseudoUserRequest request, @CurrentUser User currentUser) {
        log.info("Received new create user request");

        request.setRequestingUser(currentUser.getId());

        CreatePseudoUserResponse response = new CreatePseudoUserUseCase(request, userRepository, repository, chapterRepository).execute();

        if (response.successful) {
            return ResponseEntity.created(URI.create("userrequest/" + response.requestNumber)).body(new Gson().toJson(response));
        } else {
            return ResponseEntity.badRequest().body(new Gson().toJson(response));
        }
    }
}
