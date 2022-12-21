package com.paulograbin.confirmation.userequest.usecases.reminder;

import com.paulograbin.confirmation.userequest.UserRequest;
import com.paulograbin.confirmation.userequest.UserRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;


public class SendReminderUsecase {

    private static final Logger logger = LoggerFactory.getLogger(SendReminderUsecase.class);

    private final SendReminderRequest request;
    private final SendReminderResponse response;
    private final UserRequestRepository repository;

    public SendReminderUsecase(SendReminderRequest request, UserRequestRepository repository) {
        this.response = new SendReminderResponse();

        this.request = request;
        this.repository = repository;

    }

    public SendReminderResponse execute() {
        logger.info("Executing use case with request {}", request);

        if (isValid()) {
            sendReminderToUser();
        } else {
            setErrors();
        }

        logger.info("Reminder response {}", response);
        return response;
    }

    private void sendReminderToUser() {
        Optional<UserRequest> byEmail = repository.findByEmail(request.getUserEmail());
        UserRequest userRequest = byEmail.get();

        if (userRequest.getConversionDate() != null) {
            response.successful = false;
            response.userAlreadyCreated = true;

            return;
        }

        userRequest.setExpirationDate(LocalDateTime.now().plusWeeks(1));

        repository.save(userRequest);

        response.successful = true;
        response.userEmail = userRequest.getEmail();
        response.code = userRequest.getCode().toString();

    }

    private boolean isValid() {
        return true;
    }

    private void setErrors() {

    }
}
