package com.paulograbin.confirmation.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Data
public class EventDTO {

    private int id;
    private String title;
    private String description;
    private String address;
    private UserSimpleDTO creator;
    private Boolean published;
    private ChapterDTO chapter;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime creationDate;

}
