package com.paulograbin.confirmation.usecases.event.creation;

import com.paulograbin.confirmation.domain.User;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;


@Data
public class EventCreationRequest {

    @NotBlank
    @Size(min = 3)
    private String title;

    @NotBlank
    @Size(min = 3)
    private String description;

    @NotBlank
    private String address;
    private boolean published;

    @NotBlank
    private String date;
    private LocalTime time;

    private User creator;


}
