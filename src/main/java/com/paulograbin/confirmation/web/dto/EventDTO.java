package com.paulograbin.confirmation.web.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class EventDTO {

    private int id;
    private String title;
    private String description;
    private String address;
    private UserDTO creator;
    private LocalDateTime dateTime;
    private LocalDateTime creationDate;

}
