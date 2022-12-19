package com.paulograbin.confirmation.featureflag;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Comparator;


public class CompareToTests {

    @Test
    public void testOne() {
        BigDecimal smaller = BigDecimal.ONE;
        BigDecimal bigger = BigDecimal.TEN;

        Assertions.assertThat(bigger.compareTo(smaller)).isEqualTo(1);

        Comparator<BigDecimal> comparator = Comparator.comparing(x -> x, BigDecimal::compareTo);
        System.out.println(comparator.compare(bigger, smaller));
    }

    @Test
    void smallerMinusOne() {
        BigDecimal smaller = BigDecimal.ONE;
        BigDecimal bigger = BigDecimal.TEN;

        Assertions.assertThat(smaller.compareTo(bigger)).isEqualTo(-1);
    }

    @Test
    void equalZero() {
        BigDecimal smaller = BigDecimal.ONE;
        BigDecimal smallerAnother = BigDecimal.ONE;

        Assertions.assertThat(smaller.compareTo(smallerAnother)).isEqualTo(0);
    }
}
