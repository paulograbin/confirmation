package com.paulograbin.confirmation;

import com.paulograbin.confirmation.domain.Event;
import com.paulograbin.confirmation.domain.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class ParticipationTests {

    @Test
    void creatorGetsConfirmedAutomaticallyUponCreation() {
        User mc = new User("mc@gvs.com.br", "MC", "GVS", "aaa");

        LocalDateTime date = LocalDateTime.of(2019, 11, 21, 14, 0);
        Event e = new Event("Evento teste", "", mc, date);

//        System.out.println(e.getCreator());
        System.out.println(e.getTitle());
        System.out.println(e.getId());
        System.out.println(e.getParticipants());
    }
}
