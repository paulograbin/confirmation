package com.paulograbin.confirmation.passwordreset.creation;

import com.paulograbin.confirmation.passwordreset.PasswordRequest;
import com.paulograbin.confirmation.passwordreset.PasswordResetRepository;
import com.paulograbin.confirmation.service.mail.EmailService;
import com.paulograbin.confirmation.user.User;
import com.paulograbin.confirmation.user.UserRepository;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class ResetPasswordUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordUseCase.class);
    private static final int DURATION_IN_MINUTES = 30;

    private final ResetPasswordRequest request;
    private final ResetPasswordResponse response;
    private final PasswordResetRepository repository;
    private final UserRepository userRepository;

    private final EmailService emailService;

    public ResetPasswordUseCase(ResetPasswordRequest request, PasswordResetRepository repository, UserRepository userRepository, EmailService emailService) {
        this.request = request;
        this.repository = repository;
        this.userRepository = userRepository;
        this.emailService = emailService;

        response = new ResetPasswordResponse();
    }

    public ResetPasswordResponse execute() {
        logger.info("Executing test case with {}", request);

        if (isValid()) {
            doIt();
        } else {
            setErrors();
        }

        logger.info("Reset password returned {}", response);
        return response;
    }

    private void setErrors() {
        if (!EmailValidator.getInstance().isValid(request.emailAddress)) {
            response.invalidAddress = true;
        }
    }

    private void doIt() {
        if (!userRepository.existsByEmail(request.emailAddress)) {
            // Even though the email does not exist, we return true to the frontend so avoid showing the email is not used
            response.successful = true;
            return;
        }

        cancelOtherRequestsFromCurrentUser(request.emailAddress);

        var passwordRequest = new PasswordRequest();
        passwordRequest.setEmailAddress(request.emailAddress);
        passwordRequest.setCode(UUID.randomUUID());
        passwordRequest.setCreationDate(LocalDateTime.now());

        var expirationDate = LocalDateTime.now().plus(DURATION_IN_MINUTES, ChronoUnit.MINUTES);
        passwordRequest.setExpirationDate(expirationDate);

        repository.save(passwordRequest);

        Optional<User> byUsernameOrEmail = userRepository.findByUsernameOrEmail(request.emailAddress, request.emailAddress);
        User user = byUsernameOrEmail.get();

        //send email
        emailService.sendForgotPasswordMail(passwordRequest, user);

        response.successful = true;
        logger.info("Email sent with request code {}", passwordRequest.getCode());
    }

    private void cancelOtherRequestsFromCurrentUser(String emailAddress) {
        List<PasswordRequest> byEmailAddress = repository.findByEmailAddress(emailAddress);

        for (var passwordRequest : byEmailAddress) {
            if (isNotExpired(passwordRequest.getExpirationDate())) {
                logger.info("Canceling valid request for email {}, {}-{}", emailAddress, passwordRequest.getId(), passwordRequest.getCode());
                passwordRequest.setExpirationDate(LocalDateTime.now());

                repository.save(passwordRequest);
            }
        }
    }

    private boolean isNotExpired(LocalDateTime expirationDate) {
        return LocalDateTime.now().isBefore(expirationDate);
    }

    private boolean isValid() {
        return EmailValidator.getInstance().isValid(request.emailAddress);
    }
}
