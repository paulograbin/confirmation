package com.paulograbin.confirmation.usecases.event.creation;


import lombok.Data;

@Data
public class EventCreationResponse {

    public String errorMessage = "";

    public boolean successful;
    public Long createdEventId;

    public boolean invalidTitle;
    public boolean invalidDescription;
    public boolean invalidAddress;
    public boolean invalidTime;
    public boolean invalidChapter;
    public boolean invalidDate;
    public boolean invalidCreator;
    public boolean duplicated;
}

