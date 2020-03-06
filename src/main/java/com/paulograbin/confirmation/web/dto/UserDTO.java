package com.paulograbin.confirmation.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime creationDate;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime modificationDate;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime inactivatedIn;
    private boolean active;
    private boolean master;
    private List<RoleDTO> roles;

}
