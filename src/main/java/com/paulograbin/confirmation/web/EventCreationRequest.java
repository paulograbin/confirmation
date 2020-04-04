package com.paulograbin.confirmation.web;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;


@Data
public class EventCreationRequest {

    private String title;
    private String description;
    private String address;
    private boolean published;
    private LocalDate date;
    private LocalTime time;

}
