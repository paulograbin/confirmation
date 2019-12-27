package com.paulograbin.confirmation.web.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class EventDTO {

    private int id;
    private String title;
    private String address;
    private String creatorName;
    private LocalDateTime dateTime;
    private LocalDateTime creationDate;

}
