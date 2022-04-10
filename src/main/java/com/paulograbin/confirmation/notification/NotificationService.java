package com.paulograbin.confirmation.notification;

public interface NotificationService {
    void sendAlertAsync(String messge);

    void sendAlert(String messageText);
}
