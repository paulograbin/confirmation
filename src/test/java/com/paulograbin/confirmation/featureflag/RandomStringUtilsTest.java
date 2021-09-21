package com.paulograbin.confirmation.featureflag;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

public class RandomStringUtilsTest {

    @Test
    public void test() {
        System.out.println(RandomStringUtils.randomPrint(20));
        System.out.println(RandomStringUtils.random(20));
        System.out.println(RandomStringUtils.random(20));
        System.out.println(RandomStringUtils.random(20));
        System.out.println();

        System.out.println(RandomStringUtils.randomAscii(20));
        System.out.println(RandomStringUtils.randomAscii(20));
        System.out.println(RandomStringUtils.randomAscii(20));
        System.out.println(RandomStringUtils.randomAscii(20));
        System.out.println();

        System.out.println(RandomStringUtils.randomGraph(20));
        System.out.println(RandomStringUtils.randomGraph(20));
        System.out.println(RandomStringUtils.randomGraph(20));
        System.out.println(RandomStringUtils.randomGraph(20));
        System.out.println();

        System.out.println(RandomStringUtils.randomAlphabetic(20));
        System.out.println(RandomStringUtils.randomAlphabetic(20));
        System.out.println(RandomStringUtils.randomAlphabetic(20));
        System.out.println(RandomStringUtils.randomAlphabetic(20));
        System.out.println();

        System.out.println(RandomStringUtils.random(20, false, false));
        System.out.println(RandomStringUtils.random(20, false, false));
        System.out.println(RandomStringUtils.random(20, false, false));
        System.out.println(RandomStringUtils.random(20, false, false));
        System.out.println();

        System.out.println(RandomStringUtils.random(20, true, false));
        System.out.println(RandomStringUtils.random(20, true, false));
        System.out.println(RandomStringUtils.random(20, true, false));
        System.out.println(RandomStringUtils.random(20, true, false));
        System.out.println();

        System.out.println(RandomStringUtils.random(20, true, true));
        System.out.println(RandomStringUtils.random(20, true, true));
        System.out.println(RandomStringUtils.random(20, true, true));
        System.out.println(RandomStringUtils.random(20, true, true));
        System.out.println();
    }

}
