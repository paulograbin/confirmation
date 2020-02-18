package com.paulograbin.confirmation.web.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ParticipationWithoutEventDTO {

    long id;
    private UserDTO user;
    private String status;
    private LocalDateTime invitationDate;
    private LocalDateTime confirmationDate;

}
