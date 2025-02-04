package com.paulograbin.confirmation.featureflag;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;


public class CompareToTests {

    @Test
    public void testOne() {
        BigDecimal smaller = BigDecimal.ONE;
        BigDecimal bigger = BigDecimal.TEN;

        assertThat(bigger.compareTo(smaller)).isEqualTo(1);

        Comparator<BigDecimal> comparator = Comparator.comparing(x -> x, BigDecimal::compareTo);
        System.out.println(comparator.compare(bigger, smaller));
    }

    @Test
    void smallerMinusOne() {
        BigDecimal smaller = BigDecimal.ONE;
        BigDecimal bigger = BigDecimal.TEN;

        assertThat(smaller.compareTo(bigger)).isEqualTo(-1);
    }

    @Test
    void equalZero() {
        BigDecimal smaller = BigDecimal.ONE;
        BigDecimal smallerAnother = BigDecimal.ONE;

        assertThat(smaller.compareTo(smallerAnother)).isEqualTo(0);
    }

    @Test
    void name() {
        var bigDecimalNegative = BigDecimal.valueOf(-50);
        var bigDecimalPositive = BigDecimal.valueOf(50);
        var zero = BigDecimal.ZERO;

        int signum = bigDecimalPositive.signum();
        int signum1 = bigDecimalNegative.signum();
        int signum2 = zero.signum();

        assertThat(signum).isEqualTo(1);
        assertThat(signum1).isEqualTo(-1);
        assertThat(signum2).isEqualTo(0);
    }

    @Test
    void compareToReturnZero() {
        var first = BigDecimal.valueOf(50);
        var second = BigDecimal.valueOf(50);

        int compareResult = first.compareTo(second);

        assertThat(compareResult).isEqualTo(0);
    }

    @Test
    void compareToReturnNegative() {
        var first = BigDecimal.valueOf(5);
        var second = BigDecimal.valueOf(50);

        int compareResult = first.compareTo(second);

        assertThat(compareResult).isEqualTo(-1);
    }

    @Test
    void compareToReturnPositive() {
        var first = BigDecimal.valueOf(50);
        var second = BigDecimal.valueOf(5);

        int compareResult = first.compareTo(second);

        assertThat(compareResult).isEqualTo(1);
    }
}
