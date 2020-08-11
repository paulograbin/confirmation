package com.paulograbin.confirmation.web.dto;

import lombok.Data;

import java.util.List;


@Data
public class ChapterDTO {

    private long id;
    private String name;
    private List<UserDTO> members;

}
