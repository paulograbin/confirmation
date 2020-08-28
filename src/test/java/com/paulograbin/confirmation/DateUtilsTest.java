package com.paulograbin.confirmation;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateUtilsTest {

    @Test
    void name11() {
        String inputDateString = "28-05-1997";

        LocalDate dateFromString = DateUtils.getDateFromString(inputDateString);

        assertThat(dateFromString.getDayOfMonth()).isEqualTo(28);
        assertThat(dateFromString.getMonthValue()).isEqualTo(5);
        assertThat(dateFromString.getYear()).isEqualTo(1997);
    }

    @Test
    void name1() {
        String inputDateString = "28-May-1997";

        LocalDate dateFromString = DateUtils.getDateFromString(inputDateString);

        assertThat(dateFromString.getDayOfMonth()).isEqualTo(28);
        assertThat(dateFromString.getMonthValue()).isEqualTo(5);
        assertThat(dateFromString.getYear()).isEqualTo(1997);
    }

    @Test
    void name2() {
        String inputDateString = "30/08/2020";

        LocalDate dateFromString = DateUtils.getDateFromString(inputDateString);

        assertThat(dateFromString.getDayOfMonth()).isEqualTo(30);
        assertThat(dateFromString.getMonthValue()).isEqualTo(8);
        assertThat(dateFromString.getYear()).isEqualTo(2020);
    }

    @Test
    void american() {
        String inputDateString = "2020/08/30";

        LocalDate dateFromString = DateUtils.getDateFromString(inputDateString);

        assertThat(dateFromString.getDayOfMonth()).isEqualTo(30);
        assertThat(dateFromString.getMonthValue()).isEqualTo(8);
        assertThat(dateFromString.getYear()).isEqualTo(2020);
    }

    @Test
    void americanDois() {
        String inputDateString = "20200830";

        LocalDate dateFromString = DateUtils.getDateFromString(inputDateString);

        assertThat(dateFromString.getDayOfMonth()).isEqualTo(30);
        assertThat(dateFromString.getMonthValue()).isEqualTo(8);
        assertThat(dateFromString.getYear()).isEqualTo(2020);
    }

    @Test
    void invalid() {
        String inputDateString = "dasdadsda";

        assertThrows(DateTimeParseException.class,
                () -> DateUtils.getDateFromString(inputDateString));
    }
}
