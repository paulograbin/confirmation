package com.paulograbin.confirmation.helper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateHelper {

    public static String makeFutureDate() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        return parseDateAsString(tomorrow);
    }
    public static String makePastDate() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.minusDays(1);

        return parseDateAsString(tomorrow);
    }

    public static String parseDateAsString(LocalDate date) {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(date);
    }
}
