package com.paulograbin.confirmation.featureflag;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LongTests {

    @Test
    void givenTwoValid__mustCalculate() {
        Long a = 5L;
        Long b = 7L;

        long min = Long.min(a, b);

        assertThat(min).isEqualTo(5L);
    }

    @Test()
    void givenFirstNull__mustThrowNullPointer() {
        Long a = null;
        Long b = 7L;

        assertThatThrownBy(() -> {
            long min = Long.min(a, b);
            Assertions.fail("Should not reach this point");
        }).isInstanceOf(NullPointerException.class);
    }

    @Test
    void givenSecondNull__mustThrowNullPointer() {
        Long a = 7L;
        Long b = null;

        assertThatThrownBy(() -> {
            long min = Long.min(a, b);
            Assertions.fail("Should not reach this point");
        }).isInstanceOf(NullPointerException.class);
    }
}
