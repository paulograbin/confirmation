package com.paulograbin.confirmation.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.stream.Collectors;


@Service
public class TelegramNotificationService implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(TelegramNotificationService.class);

    @Value("${telegram.bot.key}")
    public String TELEGRAM_BOT_KEY;

    @Value("${telegram.group.id}")
    public String TELEGRAM_GROUP_ID;


    @Override
    public void sendAlertAsync(String messge) {
        var urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
        urlString = String.format(urlString, TELEGRAM_BOT_KEY, TELEGRAM_GROUP_ID, messge);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .timeout(Duration.ofSeconds(1))
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(logger::info);
    }

    @Override
    public void sendAlert(String messageText) {
        var urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
        urlString = String.format(urlString, TELEGRAM_BOT_KEY, TELEGRAM_GROUP_ID, messageText);

        try {
            var url = new URL(urlString);
            var conn = url.openConnection();
            var is = new BufferedInputStream(conn.getInputStream());

            var text = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            logger.info("Telegram request returned {}", text);
        } catch (IOException e) {
            logger.error("Telegram request failed: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
