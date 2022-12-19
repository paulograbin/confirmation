package com.paulograbin.confirmation.featureflag;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RegexTests {

    final Pattern pattern = Pattern.compile("^\\w+,\\d+$");
//    final Pattern pattern = Pattern.compile(".*,[0-9]*");

    @Test
    void name() {
        assertResult("5054772120812,4", true);
        assertResult("5053744478128,3", true);
        assertResult("123123123123,2", true);
        assertResult("300617127,1", true);
        assertResult("300412426,2", true);
        assertResult("aaaaaaaa,2", true);
        assertResult("aaaaa,1111", true);
        assertResult("5054772120812,4", true);
        assertResult("5054773239582,1", true);

        assertResult("aaaaa", false);
        assertResult("5054773239582,adada1424242", false);
        assertResult("300412426-2", false);
        assertResult("aaaaa", false);
        assertResult("aaaaa,aaaaaa", false);
        assertResult("5054773239582,adada1", false);
        assertResult("aaaaaaaa,2543aa", false);
        assertResult("sadasd$#,2543aa", false);
        assertResult("sadasd$#+2543aa", false);
        assertResult("300412426+2", false);
    }

    private void assertResult(String stringToTest, boolean expectedResult) {
        Matcher matcher = pattern.matcher(stringToTest);
        boolean result = matcher.find();

        assertThat(result).isEqualTo(expectedResult);
    }
}
