package com.paulograbin.confirmation.service.mail;

import com.paulograbin.confirmation.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!production")
public class LoggerEmailService implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(LoggerEmailService.class);

    @Override
    public void sendPasswordChangedMail(User userFromDatabase) {
        logger.info("Sending password changed mail to {}", userFromDatabase.getEmail());
    }

}
