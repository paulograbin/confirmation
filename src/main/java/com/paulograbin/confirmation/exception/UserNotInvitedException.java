package com.paulograbin.confirmation.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserNotInvitedException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(UserNotInvitedException.class);

    public UserNotInvitedException(String message) {
        super(message);

        log.error(message);
    }
}
