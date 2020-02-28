package com.paulograbin.confirmation.web.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class UserDTO {

    private int id;
    private String firstName;
    private String lastName;
    private String username;
    private List<ChapterDTO> chapter;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private LocalDateTime inactivatedIn;
    private boolean active;
    private boolean master;
    private List<RoleDTO> roles;

}
