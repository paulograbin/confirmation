package com.paulograbin.confirmation.featureflag;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class DateTimeFormatterTests {

    private static final String DEFAULT_DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss:SS";

    @Test
    public void test() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);

        LocalDateTime now = LocalDateTime.now();

        System.out.println(now);
        System.out.println(dateTimeFormatter.format(now));
    }
}
