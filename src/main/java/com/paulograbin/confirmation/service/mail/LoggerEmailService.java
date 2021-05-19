package com.paulograbin.confirmation.service.mail;

import com.paulograbin.confirmation.passwordreset.PasswordRequest;
import com.paulograbin.confirmation.user.User;
import com.paulograbin.confirmation.userequest.UserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Profile("!production")
public class LoggerEmailService implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(LoggerEmailService.class);

    @Override
    public void sendEventCreatedMail(Map<String, String> emailsAndNames, String chapterName) {

    }

    @Override
    public void sendPasswordChangedMail(User userFromDatabase) {
        logger.info("Sending password changed mail to {}", userFromDatabase.getEmail());
    }

    @Override
    public void sendUserRequestCreatedMail(UserRequest userRequest) {
        logger.info("Sending mail to let the person know a new user has been requested for him/her with id {}", userRequest.getId());
    }

    @Override
    public void sendForgotPasswordMail(PasswordRequest passwordRequest, User user) {
        logger.info("Sending email to reset password for user {}", user.getEmail());
    }
}
