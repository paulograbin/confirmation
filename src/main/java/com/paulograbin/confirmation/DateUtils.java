package com.paulograbin.confirmation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class DateUtils {

    private static final DateTimeFormatter americanPattern = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final DateTimeFormatter americanPatternAgain = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter standardPattern = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
     private static final DateTimeFormatter formatter222 = DateTimeFormatter.ofPattern("dd/MMM/yyyy");
    private static final DateTimeFormatter formatter22 = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static LocalDate getDateFromString(String dateString) {
        DateTimeFormatterBuilder dateTimeFormatterBuilder = new DateTimeFormatterBuilder();

        dateTimeFormatterBuilder.appendOptional(americanPattern);
        dateTimeFormatterBuilder.appendOptional(americanPatternAgain);
        dateTimeFormatterBuilder.appendOptional(standardPattern);
        dateTimeFormatterBuilder.appendOptional(formatter2);
        dateTimeFormatterBuilder.appendOptional(formatter22);
        dateTimeFormatterBuilder.appendOptional(formatter222);

        DateTimeFormatter formatter = dateTimeFormatterBuilder.toFormatter();

        return LocalDate.parse(dateString, formatter);
    }

    public static LocalDateTime getCurrentDate() {
        ZoneId availableZoneIds = ZoneId.of("America/Sao_Paulo");
        return LocalDateTime.now(availableZoneIds);
    }
}
