package com.paulograbin.confirmation.userequest.usecases.creation;

import com.paulograbin.confirmation.DateUtils;
import com.paulograbin.confirmation.chapter.Chapter;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.userequest.UserRequest;
import com.paulograbin.confirmation.chapter.ChapterRepository;
import com.paulograbin.confirmation.persistence.UserRepository;
import com.paulograbin.confirmation.userequest.UserRequestRepository;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class CreatePseudoUserUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CreatePseudoUserUseCase.class);

    public static final int NAME_MAX_LENGTH = 250;
    public static final int NAME_MINIMUM_LENGTH = 3;

    private CreatePseudoUserRequest request;
    private CreatePseudoUserResponse response;
    private UserRepository userRepository;
    private ChapterRepository chapterRepository;
    private UserRequestRepository userRequestRepository;


    public CreatePseudoUserUseCase(CreatePseudoUserRequest request, UserRepository repository, UserRequestRepository userRequestRepository, ChapterRepository chapterRepository) {
        this.response = new CreatePseudoUserResponse();

        this.request = request;
        this.userRepository = repository;
        this.chapterRepository = chapterRepository;
        this.userRequestRepository = userRequestRepository;
    }

    public CreatePseudoUserResponse execute() {
        logger.info("Executing use case...");
        logger.info(request.toString());

        if (isValid()) {
            createUserRequest();
        } else {
            setErrors();
        }

        logger.info("User request creation response {}", response);
        return response;
    }

    private void createUserRequest() {
        UserRequest userToCreate = new UserRequest();
        userToCreate.setId(UUID.randomUUID());
        userToCreate.setEmail(request.email);
        userToCreate.setFirstName(request.firstName);
        userToCreate.setLastName(request.lastName);

        Chapter chapter = chapterRepository.findById(request.getChapterId()).get();
        userToCreate.setChapter(chapter);

        User requestingUser = userRepository.findById(request.requestingUser).get();
        userToCreate.setCreatedBy(requestingUser);

        userToCreate.setCreationDate(DateUtils.getCurrentDate());
        userToCreate.setExpirationDate(DateUtils.getCurrentDate().plusDays(1));

        userRequestRepository.save(userToCreate);

        response.successful = true;
        response.requestNumber = userToCreate.getId().toString();
    }

    private boolean isValid() {
        logger.info("Checking if request is valid");

        if (isBlank(request.firstName) || request.firstName.length() < NAME_MINIMUM_LENGTH || request.firstName.length() > NAME_MAX_LENGTH) {
            return false;
        }

        if (isBlank(request.lastName) || request.lastName.length() < NAME_MINIMUM_LENGTH || request.lastName.length() > NAME_MAX_LENGTH) {
            return false;
        }

        User requestingUser = userRepository.findById(request.requestingUser).orElse(new User());
        if (requestingUser.getId() == null) {
            return false;
        }

        if (!requestingUser.isAdmin()) {
            return false;
        }

        if (!EmailValidator.getInstance().isValid(request.email)) {
            return false;
        }

        if (userRepository.existsByEmail(request.email) || userRequestRepository.existsByEmail(request.email)) {
            return false;
        }

        if (!chapterRepository.existsById(request.getChapterId())) {
            return false;
        }

        return true;
    }

    private void setErrors() {
        logger.info("Something went wrong while validating request...");

        if (isBlank(request.firstName) || request.firstName.length() < NAME_MINIMUM_LENGTH || request.firstName.length() > NAME_MAX_LENGTH) {
            response.invalidFirstName = true;
        }

        if (isBlank(request.lastName) || request.lastName.length() < NAME_MINIMUM_LENGTH || request.lastName.length() > NAME_MAX_LENGTH) {
            response.invalidLastName = true;
        }

        if (!EmailValidator.getInstance().isValid(request.email)) {
            response.invalidEmail = true;
        } else {
            if (userRepository.existsByEmail(request.email) || userRequestRepository.existsByEmail(request.email)) {
                response.emailNotAvailable = true;
                response.errorMessage = "This email has been taken by another user";
            }
        }

        User user = userRepository.findById(request.requestingUser).orElse(new User());
        if (user.getId() == null) {
            response.invalidRequestingUser = true;
        }

        if (!user.isAdmin() || !user.isMaster()) {
            response.errorMessage = "This action can only be performed by an admin or an MC";
            response.notAllowed = true;
        }
    }
}
