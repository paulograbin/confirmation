package com.paulograbin.confirmation.web.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Data
public class EventDTO {

    private int id;
    private String title;
    private String description;
    private String address;
    private UserDTO creator;
    private Boolean published;
    private LocalDate date;
    private LocalTime time;
    private LocalDateTime creationDate;

}
