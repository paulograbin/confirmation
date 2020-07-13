package com.paulograbin.confirmation.service.mail;

import com.paulograbin.confirmation.domain.User;

public interface EmailService {

    void sendPasswordChangedMail(User userFromDatabase);

}
