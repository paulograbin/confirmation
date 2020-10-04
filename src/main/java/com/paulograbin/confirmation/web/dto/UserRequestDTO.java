package com.paulograbin.confirmation.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class UserRequestDTO {

    private String id;
    private String code;
    private String firstName;
    private String lastName;
    private String email;

    private String chapterId;
    private String chapterName;

    private String userId;
    private String username;

    private String createdById;
    private String createdByUsername;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime creationDate;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime expirationDate;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime convertionDate;

}
