package com.paulograbin.confirmation.event.usecases.readevent;

import lombok.Data;


@Data
public class ReadEventRequest {

    public long userId;
    public long eventId;

}
