package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.participation.ParticipationService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class ParticipationServiceIntegrationTest {

    @Resource
    private ParticipationService participationService;

    @Test
    void name() {
        var participationCount = participationService.fetchCount();
    }


}
