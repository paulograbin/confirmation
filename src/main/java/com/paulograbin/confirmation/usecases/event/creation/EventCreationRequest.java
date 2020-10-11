package com.paulograbin.confirmation.usecases.event.creation;

import com.paulograbin.confirmation.domain.User;
import lombok.Data;

import java.time.LocalTime;


@Data
public class EventCreationRequest {

    private String title;
    private String description;
    private String address;
    private String date;
    private LocalTime time;
    private boolean published;
    private long creatorId;

}
