package com.paulograbin.confirmation.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;


@Service
public class TelegramNotificationService implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(TelegramNotificationService.class);

    @Value("${telegram.bot.key}")
    public static String TELEGRAM_BOT_KEY;

    @Value("${telegram.group.id}")
    public static String TELEGRAM_GROUP_ID = "-774787175";


    public void sendAlert(String messageText) {
        var urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
        urlString = String.format(urlString, TELEGRAM_BOT_KEY, TELEGRAM_GROUP_ID, messageText);

        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            InputStream is = new BufferedInputStream(conn.getInputStream());

            String text = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            logger.info("Telegram request returned {}", text);
        } catch (IOException e) {
            logger.error("Telegram request failed: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
