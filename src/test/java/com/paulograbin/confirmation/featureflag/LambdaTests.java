package com.paulograbin.confirmation.featureflag;

import com.paulograbin.confirmation.usecases.user.harddelete.UserHardDeleteUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

class LambdaTests {

    private static final Logger logger = LoggerFactory.getLogger(LambdaTests.class);

    @Test
    void testOneA() {
        var a = Optional.of("aaa").orElse("aaa");
        var b = Optional.of("aaa").orElse(test());
    }

    @Test
    void testOneB() {
        Optional.empty().orElseGet(this::test);
    }

    @Test
    void testOneAAAA() {
        Optional.of("aaa").orElseGet(this::test);
    }

    private String test() {
        logger.info("Entrou aqui ehein...");

        return "OrElse";
    }

    @Test
    void testThree() {
        String user = makeEmptyOptional().orElse("Executing orElse");
        System.out.println(user);

        String user2 = makeEmptyOptional().orElseGet(() -> "Executing orElseGet");
        System.out.println(user2);

        String user3 = makeNonOptional().orElseGet(() -> "Executing orElseGet again");
        System.out.println(user3);
    }

    private Optional<String> makeNonOptional() {
        return Optional.of("Present!!");
    }

    private Optional<String> makeEmptyOptional() {
        return Optional.empty();
    }
}
