package com.paulograbin.confirmation.web.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class UserDTO {

    private int id;
    private String firstName;
    private String lastName;
    private String username;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private LocalDateTime inactivatedIn;
    private boolean active;
    private boolean master;

}
