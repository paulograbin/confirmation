package com.paulograbin.confirmation.userequest.usecases.convertion;

import com.paulograbin.confirmation.user.User;
import com.paulograbin.confirmation.userequest.UserRequest;
import com.paulograbin.confirmation.user.UserRepository;
import com.paulograbin.confirmation.userequest.UserRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static com.paulograbin.confirmation.DateUtils.getCurrentDate;

public class ConvertPseudoUserUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ConvertPseudoUserUseCase.class);

    private final ConvertPseudoUserRequest request;
    private final ConvertPseudoUserResponse response;
    private final UserRequestRepository repository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    private UUID requestId;

    public ConvertPseudoUserUseCase(ConvertPseudoUserRequest request, PasswordEncoder passwordEncoder, UserRequestRepository repository, UserRepository userRepository) {
        this.response = new ConvertPseudoUserResponse();

        this.encoder = passwordEncoder;
        this.request = request;
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public ConvertPseudoUserResponse execute() {
        logger.info("Executing use case with request {}", request);

        if (isValid()) {
            convertRequestToNewUser();
        } else {
            setErrors();
        }

        logger.info("Returning response {}", response);
        return response;
    }

    private boolean isValid() {
        try {
            this.requestId = UUID.fromString(request.requestNumber);
        } catch (IllegalArgumentException e) {
            return false;
        }

        Optional<UserRequest> byId = repository.findByCode(requestId);
        if (byId.isEmpty()) {
            return false;
        }

        UserRequest request = byId.get();
        if (getCurrentDate().isAfter(request.getExpirationDate())) {
            return false;
        }

        if (request.getConversionDate() != null) {
            return false;
        }

        return true;
    }

    private void setErrors() {
        try {
            this.requestId = UUID.fromString(request.requestNumber);

            Optional<UserRequest> byId = repository.findByCode(requestId);
            if (byId.isEmpty()) {
                response.errorMessage = "Requisição não encontrada";
                response.requestNotFound = true;
                return;
            }

            UserRequest request = byId.get();

            if (getCurrentDate().isAfter(request.getExpirationDate())) {
                response.errorMessage = "Essa requisição já não é mais válida";
                response.expired = true;
            }

            if (request.getConversionDate() != null) {
                response.errorMessage = "Essa requisição já não é mais válida";
                response.expired = true;
            }
        } catch (IllegalArgumentException e) {
            response.errorMessage = "Requisição não encontrada";
            response.requestNotFound = true;
        }
    }

    private void convertRequestToNewUser() {
        Optional<UserRequest> byId = repository.findByCode(requestId);

        if (byId.isEmpty()) {
            response.requestNotFound = true;
            return;
        }

        UserRequest userRequest = byId.get();

        if (userRequest.getUser() != null) { //todo or expired
            throw new RuntimeException("Request already used");
        }

        if (getCurrentDate().isAfter(userRequest.getExpirationDate())) {
            throw new RuntimeException("Request expired");
        }

        if (userRepository.existsByUsername(request.username)) {
            response.usernameNotAvailable = true;
            response.errorMessage = "Usuário não disponível";
            return;
        }

        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setUsername(this.request.username);
        user.setFirstName(this.request.firstName);
        user.setLastName(this.request.lastName);
        user.setPassword(encoder.encode(this.request.password));
        user.setCreationDate(getCurrentDate());
        user.setChapter(userRequest.getChapter());
        userRequest.getChapter().getUsers().add(user);

        userRepository.save(user);

        userRequest.setConversionDate(getCurrentDate());
        userRequest.setUser(user);
        repository.save(userRequest);

        response.requestNumber = request.requestNumber;
        response.successful = true;
        response.userId = user.getId().toString();
        response.username = user.getUsername();
        response.email = user.getEmail();
    }
}
