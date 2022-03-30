package com.paulograbin.confirmation.userequest.usecases.creation;

import com.paulograbin.confirmation.DateUtils;
import com.paulograbin.confirmation.chapter.Chapter;
import com.paulograbin.confirmation.user.User;
import com.paulograbin.confirmation.service.mail.EmailService;
import com.paulograbin.confirmation.userequest.UserRequest;
import com.paulograbin.confirmation.chapter.ChapterRepository;
import com.paulograbin.confirmation.user.UserRepository;
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

    private final CreatePseudoUserRequest request;
    private final CreatePseudoUserResponse response;
    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;
    private final UserRequestRepository userRequestRepository;
    private final EmailService emailService;

    private UserRequest userToCreate;


    public CreatePseudoUserUseCase(CreatePseudoUserRequest request, UserRepository repository, UserRequestRepository userRequestRepository, ChapterRepository chapterRepository, EmailService emailService) {
        this.response = new CreatePseudoUserResponse();

        this.request = request;
        this.userRepository = repository;
        this.chapterRepository = chapterRepository;
        this.userRequestRepository = userRequestRepository;
        this.emailService = emailService;
    }

    public CreatePseudoUserResponse execute() {
        logger.info("Executing use case...");
        logger.info(request.toString());

        if (isValid()) {
            createUserRequest();
            sendMailToUser();
        } else {
            setErrors();
        }

        logger.info("User request creation response {}", response);
        return response;
    }

    private void sendMailToUser() {
        emailService.sendUserRequestCreatedMail(userToCreate);
    }

    private void createUserRequest() {
        userToCreate = new UserRequest();
        userToCreate.setCode(UUID.randomUUID());
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
        response.id = userToCreate.getId().toString();
        response.code = userToCreate.getCode().toString();
    }

    private boolean isValid() {
        logger.info("Checking if request is valid");

        if (isBlank(request.firstName) || request.firstName.length() < NAME_MINIMUM_LENGTH || request.firstName.length() > NAME_MAX_LENGTH) {
            return false;
        }

//        if (isBlank(request.lastName) || request.lastName.length() < NAME_MINIMUM_LENGTH || request.lastName.length() > NAME_MAX_LENGTH) {
//            return false;
//        }

        User requestingUser = userRepository.findById(request.requestingUser).orElseGet(User::new);
        if (requestingUser.getId() == null) {
            return false;
        }

        if (!requestingUser.isAdmin() && !requestingUser.isMaster()) {
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

//        if (isBlank(request.lastName) || request.lastName.length() < NAME_MINIMUM_LENGTH || request.lastName.length() > NAME_MAX_LENGTH) {
//            response.errorMessage = "Sobrenome inválido";
//            response.invalidLastName = true;
//        }

        if (!EmailValidator.getInstance().isValid(request.email)) {
            response.invalidEmail = true;
        } else {
            if (userRepository.existsByEmail(request.email) || userRequestRepository.existsByEmail(request.email)) {
                response.emailNotAvailable = true;
                response.errorMessage = "This email has been taken by another user";
            }
        }

        User requestingUser = userRepository.findById(request.requestingUser).orElseGet(User::new);
        if (requestingUser.getId() == null) {
            response.invalidRequestingUser = true;
        }

        if (!requestingUser.isAdmin() && !requestingUser.isMaster()) {
            response.errorMessage = "This action can only be performed by an admin or an MC";
            response.notAllowed = true;
        }

        if (!chapterRepository.existsById(request.getChapterId())) {
            response.invalidChapter = true;
            response.errorMessage = "Invalid chapter";
        }
    }
}
