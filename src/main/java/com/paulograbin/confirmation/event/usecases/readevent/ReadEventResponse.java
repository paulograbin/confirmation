package com.paulograbin.confirmation.event.usecases.readevent;

import com.paulograbin.confirmation.web.dto.EventDetailsDTO;
import lombok.Data;


@Data
public class ReadEventResponse {

    public boolean successful;
    public EventDetailsDTO eventDetails;

    public boolean notAllowed;
    public String errorMessage;
    public boolean invalidUser;
    public boolean invalidEvent;
}
