package com.paulograbin.confirmation.service.mail;

import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.domain.UserRequest;

public interface EmailService {

    void sendPasswordChangedMail(User userFromDatabase);

    void sendUserRequestCreatedMail(UserRequest userRequest);

}
