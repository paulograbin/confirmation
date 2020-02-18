package com.paulograbin.confirmation.web.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ParticipationWithoutUserDTO {

    long id;
    private EventDTO event;
    private String status;
    private LocalDateTime invitationDate;
    private LocalDateTime confirmationDate;

}
