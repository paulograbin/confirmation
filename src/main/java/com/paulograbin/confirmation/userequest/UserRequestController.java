package com.paulograbin.confirmation.userequest;

import com.paulograbin.confirmation.chapter.ChapterRepository;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.persistence.UserRepository;
import com.paulograbin.confirmation.security.jwt.CurrentUser;
import com.paulograbin.confirmation.service.mail.EmailService;
import com.paulograbin.confirmation.userequest.usecases.convertion.ConvertPseudoUserRequest;
import com.paulograbin.confirmation.userequest.usecases.convertion.ConvertPseudoUserResponse;
import com.paulograbin.confirmation.userequest.usecases.convertion.ConvertPseudoUserUseCase;
import com.paulograbin.confirmation.userequest.usecases.creation.CreatePseudoUserRequest;
import com.paulograbin.confirmation.userequest.usecases.creation.CreatePseudoUserResponse;
import com.paulograbin.confirmation.userequest.usecases.creation.CreatePseudoUserUseCase;
import com.paulograbin.confirmation.userequest.usecases.read.ReadPseudoUserRequest;
import com.paulograbin.confirmation.userequest.usecases.read.ReadPseudoUserResponse;
import com.paulograbin.confirmation.userequest.usecases.read.ReadPseudoUserUseCase;
import com.paulograbin.confirmation.web.dto.UserRequestDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Lists;
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
import javax.websocket.server.PathParam;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


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
    private EmailService emailService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private ModelMapper modelMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserRequestDTO> readAll() {
        Iterable<UserRequest> all = repository.findAll();

        List<UserRequest> from = Lists.from(all.iterator());
        return from.stream()
                .map(r -> modelMapper.map(r, UserRequestDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ReadPseudoUserResponse> listAll(@PathVariable String id) {
        log.info("Listing user request {}", id);

        ReadPseudoUserRequest readPseudoUserRequest = new ReadPseudoUserRequest();
        readPseudoUserRequest.requestId = id;

        ReadPseudoUserResponse response = new ReadPseudoUserUseCase(readPseudoUserRequest, repository).execute();

//        if (response.successful) {
            return ResponseEntity.ok().body(response);
//        } else {
//            return ResponseEntity.badRequest().body(response);
//        }
    }

    @PostMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ConvertPseudoUserResponse> convertRequestToUser(@PathParam("id") String id, @RequestBody ConvertPseudoUserRequest request) {
        log.info("Received pseudo user convertion request");

        ConvertPseudoUserResponse response = new ConvertPseudoUserUseCase(request, passwordEncoder, repository, userRepository).execute();

        return ResponseEntity.ok().body(response);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CreatePseudoUserResponse> createUserRequest(@RequestBody CreatePseudoUserRequest request, @CurrentUser User currentUser) {
        log.info("Received new create user request");

        if (currentUser == null) {
            CreatePseudoUserResponse response = new CreatePseudoUserResponse();
            response.errorMessage = "This action can only be performed by an admin or an MC";
            response.notAllowed = true;

            return ResponseEntity.badRequest().body(response);
        }

        request.setRequestingUser(currentUser.getId());

        CreatePseudoUserResponse response = new CreatePseudoUserUseCase(request, userRepository, repository, chapterRepository).execute();

        if (response.successful) {
            Optional<UserRequest> byId = repository.findById(UUID.fromString(response.getRequestNumber()));
            UserRequest newlyCreatedRequest = byId.get();

            emailService.sendUserRequestCreatedMail(newlyCreatedRequest);
        }


        if (response.successful) {
            return ResponseEntity.created(URI.create("userrequest/" + response.requestNumber)).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
