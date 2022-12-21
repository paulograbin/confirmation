package com.paulograbin.confirmation.service.defaultdata;

import com.paulograbin.confirmation.chapter.Chapter;
import com.paulograbin.confirmation.chapter.ChapterService;
import com.paulograbin.confirmation.event.Event;
import com.paulograbin.confirmation.event.EventService;
import com.paulograbin.confirmation.service.UserService;
import com.paulograbin.confirmation.user.User;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;


@Service
@Profile("!production")
public class DevelopmentApplicationReadyHelper {

    private static final Logger log = LoggerFactory.getLogger(DevelopmentApplicationReadyHelper.class);


    private static final String ADMIN_USERNAME = "plgrabin";
    private static final String DEFAULT_ADDRESS_JOAO_CORREA = "Avenida João Corrêa, 815";


    @Resource
    private ChapterService chapterService;

    @Resource
    private UserService userService;

    @Resource
    private EventService eventService;


    @Resource
    DevelopmentDefaultData developmentDefaultData;


    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        log.info("Running development default data");

        developmentDefaultData.setDefaultRoles();
        developmentDefaultData.setDefaultChapters();
        developmentDefaultData.setDefaultAdmin();

//        setDefaultEvents();
//        setDefaultUsers();

//        setUserRequests();
//        updateUserRequestToIncludeId();
    }


    private void setDefaultEvents() {
        log.info("Checking events...");

        if (eventService.fetchCount() > 0) {
            log.info("Skipping default events.");
            return;
        }

        Chapter gvs = chapterService.fetchById(592L);
        User mc1 = userService.fetchByUsername(ADMIN_USERNAME);

        Event e01 = new Event(gvs, "Mais antigo", DEFAULT_ADDRESS_JOAO_CORREA, "Evento mais velho", mc1,
                LocalDate.of(2019, 1, 1),
                LocalTime.of(14, 0, 0));
        e01 = eventService.createEvent(e01, mc1);

        Event e02 = new Event(gvs, "Proximo", DEFAULT_ADDRESS_JOAO_CORREA, "Proximo", mc1,
                LocalDate.of(2021, 6, 20),
                LocalTime.of(14, 0, 0));
        e02 = eventService.createEvent(e02, mc1);

        Event e03 = new Event(gvs, "Futuro", DEFAULT_ADDRESS_JOAO_CORREA, "Evento do futuro", mc1,
                LocalDate.of(2021, 6, 1),
                LocalTime.of(14, 0, 0));
        e03 = eventService.createEvent(e03, mc1);

//        List<Event> upComingEvents = eventService.fetchUpComingEventsFromChapter(gvs.getId());
//        isTrue(upComingEvents.size() == 2, "Two events for GVS");
//
//        List<Event> allEventsFromGVS = eventService.fetchAllEventsFromChapter(gvs.getId());
//        isTrue(allEventsFromGVS.size() == 3, "Three events for GVS");


//        isTrue(e01.getParticipants().size() == 1, "Only one participant");
//        isTrue(e01.getParticipants().get(0).getStatus() == ParticipationStatus.CONFIRMADO, "Participant is confirmed");
//        isTrue(e01.getParticipants().get(0).getUser().getUsername().equals(ADMIN_USERNAME), "Participant is confirmed");
    }

    private void setDefaultUsers() {
        log.info("Checking users...");

        Chapter chapter_gvs = chapterService.fetchById(592L);

        User mc2 = new User("asimov", "Isaac", "Asimov", "isaac@asimov.com", "aaa");
        createUserIfDoesntExist(mc2, chapter_gvs, false);

        Chapter chapter200 = chapterService.fetchById(200L);
        User master_2 = new User("master2", "Mestre 2", "Sobrenome @", "mestre@100.com", "aaa");
        createUserIfDoesntExist(master_2, chapter200, true);

        User not_master_2 = new User("notmaster2", "Not Mestre 2", "Not Sobrenome @", "not_mestre@100.com", "aaa");
        createUserIfDoesntExist(not_master_2, chapter200, false);
    }

    private void createUserIfDoesntExist(User newUser, Chapter gvs, boolean setAsMaster) {
        try {
            userService.createUser(newUser);
            userService.assignUserToChapter(newUser.getId(), gvs.getId());

            if (setAsMaster) {
                log.info("Setting {} as master", newUser.getUsername());
                userService.setAsMaster(newUser.getId());
            }
        } catch (RuntimeException e) {
            log.info("User {} already exists, no need to create it again", newUser.getUsername());
        }
    }
}
