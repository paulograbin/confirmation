package com.paulograbin.confirmation.web;

import lombok.Value;

import java.time.LocalDate;
import java.time.LocalTime;


@Value
public class EventCreationRequest {

    private String title;
    private String description;
    private String address;
    private LocalDate date;
    private LocalTime time;

}
