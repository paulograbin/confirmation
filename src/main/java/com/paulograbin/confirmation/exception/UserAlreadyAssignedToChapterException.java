package com.paulograbin.confirmation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAlreadyAssignedToChapterException extends RuntimeException {

    public UserAlreadyAssignedToChapterException(String message) {
        super(message);
    }
}
