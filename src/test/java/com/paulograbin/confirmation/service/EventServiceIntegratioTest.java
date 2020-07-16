package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.domain.Chapter;
import com.paulograbin.confirmation.domain.Event;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.persistence.EventRepository;
import com.paulograbin.confirmation.usecases.ChapterCreationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class EventServiceIntegratioTest {

    private static final Logger logger = LoggerFactory.getLogger(EventServiceIntegratioTest.class);

    @Resource
    EventService eventService;

    @Resource
    EventRepository eventRepository;

    @Resource
    UserService userService;

    @Resource
    ChapterService chapterService;

    private Event event;

    @BeforeEach
    void setUp() {
        event = null;

        for (Event event1 : eventRepository.findAll()) {
            logger.info("Found chapter {}", event1.toString());
        }

    }

    @Test
    void contextLoads() {
        assertThat(eventService).isNotNull();
        assertThat(eventRepository).isNotNull();

//        assertThat(eventRepository.count()).isEqualTo(0);
    }

    @Test
    public void duringCreation_eventReceivesId() {
        givenEvent();

        assertThat(event.getId()).isNotNull();
        assertThat(event.getCreationDate()).isNotNull();
    }

    @Test
    void afterCreationEventHasNoParticipants() {
        givenEvent();

        assertThat(event.getParticipants()).hasSize(0);
    }

    private void givenEvent() {
        event = makeEvent();
        insertEventIntoDatabase(event);
    }

    private void insertEventIntoDatabase(Event event) {
        eventService.createEvent(event, event.getCreator());
    }

    private Event makeEvent() {
        int randomNumber = new Random().nextInt(1000);

        var event = new Event();

        event.setCreator(makeUserCreator());
        event.setTitle("Test event " + randomNumber);
        event.setDescription("Test event description " + randomNumber);
        event.setAddress("Test event address " + randomNumber);
        event.setDate(LocalDate.now().plusDays(1));
        event.setTime(LocalTime.now());
        event.setPublished(false);

        return event;
    }

    private User makeUserCreator() {
        int randomNumber = new Random().nextInt(1000);

        User user = new User();

        user.setFirstName("Paulo" + randomNumber);
        user.setLastName("Grabin" + randomNumber);
        user.setUsername("plgrabin" + randomNumber);
        user.setEmail("plgrabin@gmail.com" + randomNumber);
        user.setPassword("123" + randomNumber);


        ChapterCreationRequest chapterCreationRequest = new ChapterCreationRequest(666L, "Test Chapter");
        Chapter chapter = chapterService.createChapter(chapterCreationRequest);
        user.setChapter(chapter);

        userService.createUser(user);

        return user;
    }

}
