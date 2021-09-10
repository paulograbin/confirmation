package com.paulograbin.confirmation.featureflag;

import org.apache.commons.lang3.BooleanUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


class MapTests {

    private final Map<String, Boolean> featureMap = new HashMap<>();

    @BeforeEach
    void setUp() {
        featureMap.put("AAA", Boolean.TRUE);
        featureMap.put("BBB", Boolean.TRUE);
    }

    @Test
    void existingKey() {
        String featureKey = "AAA";

        Boolean aBoolean = featureMap.computeIfPresent(featureKey, (s, aBoolean1) -> !aBoolean1);
        System.out.println("Returned " + aBoolean);
    }

    @Test
    void nonExistingKey() {
        String featureKey = "111";

        Boolean aBoolean = featureMap.computeIfPresent(featureKey, (s, aBoolean1) -> !aBoolean1);
        System.out.println("Returned " + aBoolean);
    }

    @Test
    void name() {
        System.out.println(BooleanUtils.isTrue(null));
        System.out.println(BooleanUtils.isTrue(true));
        System.out.println(BooleanUtils.isTrue(false));
    }
}