package com.paulograbin.confirmation.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UsernameNotAvailableException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(UsernameNotAvailableException.class);

    public UsernameNotAvailableException(String message) {
        super(message);

        log.error(message);
    }
}
