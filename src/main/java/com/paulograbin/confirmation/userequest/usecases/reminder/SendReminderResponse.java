package com.paulograbin.confirmation.userequest.usecases.reminder;


import lombok.Data;

@Data
public class SendReminderResponse {

    public boolean successful;
    public String userEmail;
    public String code;

    public boolean userAlreadyCreated;
}
