package com.paulograbin.confirmation.usecases.event.creation;

import com.paulograbin.confirmation.domain.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;


@Data
@Builder(setterPrefix = "with")
public class EventCreationRequest {

    private String title;
    private String description;
    private String address;
    private String date;
    private LocalTime time;
    private boolean published;
    private User creator;

}
