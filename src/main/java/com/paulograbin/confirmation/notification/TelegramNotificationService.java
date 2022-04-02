package com.paulograbin.confirmation.notification;

import com.paulograbin.confirmation.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    public static final String TELEGRAM_BOT_KEY = "5262818239:AAG48ecTKv5FnwcyykbC7illPRjgdmo_dxU";
    public static final String TELEGRAM_GROUP_ID = "-774787175";


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


    private static final int FIVE_SECONDS = 5 * 1000;
    private static final int DELAY_BETWEEN_SCANS = FIVE_SECONDS;

//    @Scheduled(fixedDelay = DELAY_BETWEEN_SCANS)
    void fetchUpdates() {
        logger.info("Fetching updates " + DateUtils.getCurrentDate());
//        String nextId = makeNextUpdateIdToFetch();
        int nextId = 0;

        logger.info("Retrieving updates {}", nextId);
        String urlString = "https://api.telegram.org/bot5262818239:AAG48ecTKv5FnwcyykbC7illPRjgdmo_dxU/getUpdates?offset=" + nextId;

        var responseString = new RestTemplate().postForObject(urlString, null, String.class);
        logger.info(responseString);
    }

}
