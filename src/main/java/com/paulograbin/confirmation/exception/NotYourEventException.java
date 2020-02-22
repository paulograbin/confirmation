package com.paulograbin.confirmation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.FORBIDDEN)
public class NotYourEventException extends RuntimeException {

    public NotYourEventException(String message) {
        super(message);
    }
}
