package com.paulograbin.confirmation.event.usecases.readevent;

import com.paulograbin.confirmation.web.dto.EventDetailsDTO;
import lombok.Data;


@Data
public class ReadEventResponse {

    public EventDetailsDTO eventDetails = new EventDetailsDTO();

    public boolean successful;

    public boolean notAllowed;
    public String errorMessage;
    public boolean invalidUser;
    public boolean invalidEvent;
    public boolean canChange;
    public boolean isInThePast;
    public boolean creating;
}
