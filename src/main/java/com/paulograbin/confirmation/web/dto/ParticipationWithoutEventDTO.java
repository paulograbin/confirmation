package com.paulograbin.confirmation.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ParticipationWithoutEventDTO {

    long id;
    private UserDTO user;
    private String status;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime invitationDate;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime confirmationDate;

}
