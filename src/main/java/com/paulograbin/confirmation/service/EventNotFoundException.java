package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.exception.NotFoundException;


public class EventNotFoundException extends NotFoundException {

    public EventNotFoundException(String message) {
        super(message);
    }
}
