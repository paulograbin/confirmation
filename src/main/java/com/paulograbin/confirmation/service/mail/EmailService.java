package com.paulograbin.confirmation.service.mail;

import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.userequest.UserRequest;

import java.util.Map;

public interface EmailService {

    void sendEventCreatedMail(Map<String, String> emailsAndNames, String chapterName);

    void sendPasswordChangedMail(User userFromDatabase);

    void sendUserRequestCreatedMail(UserRequest userRequest);

}
