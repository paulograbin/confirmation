package com.paulograbin.confirmation.usecases.event.creation;

import com.paulograbin.confirmation.domain.Event;

public class EventCreationResponse {

    public String errorMessage = "";

    public boolean successful;
    public long createdEventId;

    public boolean invalidTitle;
    public boolean invalidDescription;
    public boolean invalidAddress;
    public boolean invalidTime;
    public boolean invalidChapter;
    public boolean invalidDate;
}

