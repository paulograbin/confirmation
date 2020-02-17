package com.paulograbin.confirmation.web.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class UserDTO {

    private int id;
    private String firstName;
    private String lastName;
    private String username;
    private String creationDate;
    private String modificationDate;
    private String inactivatedIn;
    private boolean active;
    private boolean master;

}
