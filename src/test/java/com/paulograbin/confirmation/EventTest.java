package com.paulograbin.confirmation;

import com.paulograbin.confirmation.persistence.EventRepository;
import com.paulograbin.confirmation.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class EventTest {

    @Resource
    private EventRepository eventRepository;

    @Resource
    private UserRepository userRepository;


    @Test
    void name() {
//        User mc = new User("mc@gvs.com.br", "MC", "GVS", "", "aaa");
//
//        Event e = new Event(gvs, "Sessão sábado", "", "", mc, LocalDateTime.of(2019, 11, 21, 14, 0));
//
//        userRepository.save(mc);
//        eventRepository.save(e);
//
//        System.out.println(e.getParticipants());
//        assertEquals(e.getParticipants().size(), 1);
    }
}
