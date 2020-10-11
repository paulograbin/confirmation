package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.participation.ParticipationService;
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
    }


}
