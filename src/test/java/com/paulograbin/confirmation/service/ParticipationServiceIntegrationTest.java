package com.paulograbin.confirmation.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ParticipationServiceIntegrationTest {

    @Resource
    private ParticipationService participationService;

    @Test
    void name() {
        var participationCount = participationService.fetchCount();

        Assertions.assertThat(participationCount).isEqualTo(0);
    }


}
