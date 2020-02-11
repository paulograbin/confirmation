package com.paulograbin.confirmation.web.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class EventDetailsDTO {

    private int id;
    private String title;
    private String address;
    private int creatorId;
    private String creatorFirstName;
    private String creatorLastName;
    private LocalDateTime dateTime;
    private LocalDateTime creationDate;
    private List<ParticipationDTO> participants;

}
