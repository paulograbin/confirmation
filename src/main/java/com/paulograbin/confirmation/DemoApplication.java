package com.paulograbin.confirmation;

import com.paulograbin.confirmation.persistence.EventRepository;
import com.paulograbin.confirmation.persistence.ParticipationRepository;
import com.paulograbin.confirmation.persistence.UserRepository;
import com.paulograbin.confirmation.service.EventService;
import com.paulograbin.confirmation.service.ParticipationService;
import com.paulograbin.confirmation.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }


    @Resource
    private EventRepository eventRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private ParticipationRepository participationRepository;

    @Resource
    private UserService userService;

    @Resource
    private EventService eventService;

    @Resource
    private ParticipationService participationService;


    @Override
    public void run(String... args) throws Exception {
        User mc1 = new User("plgrabin", "Paulo", "Grabin", "aaa");
        User mc2 = new User("paulograbin", "Paulo 22", "Grabin 222", "bbb");
        User mc3 = new User("primeiroconselheiro", "Primeiro", "Conselheiro", "ccc");
        User mc4 = new User("segundoconselheiro", "Segundo", "Conselheiro", "ddd");
        User mc5 = new User("visitante", "visitante", "numero 1", "eee");

        mc1 = userService.createUser(mc1);
        mc2 = userService.createUser(mc2);
        mc3 = userService.createUser(mc3);
        mc4 = userService.createUser(mc4);
        mc5 = userService.createUser(mc5);

        Event e = new Event("Cerimonia 1", "Avenida João Correa 815", mc1, LocalDateTime.of(2019, 11, 21, 14, 0));
        Event e2 = new Event("Cerimonia 2", "Avenida João Correa 815", mc1, LocalDateTime.of(2019, 11, 28, 14, 0));
        e = eventService.createEvent(e);
        e2 = eventService.createEvent(e2);

        eventService.invite2(mc3.getId(), e.getId());
        eventService.invite2(mc4.getId(), e.getId());
        eventService.invite2(mc5.getId(), e.getId());

        List<Participation> participants = e.getParticipants();
        participationRepository.saveAll(participants);

        participationRepository.findAll();


//        User firstInvited = new User("firstinvited", "First", "Invited", "aaa");
//        e.addParticipant(firstInvited);
//        userRepository.save(firstInvited);
//        participationRepository.saveAll(participants);
//
//        e.confirmParticipant(firstInvited);
//        participationRepository.saveAll(participants);
    }
}
