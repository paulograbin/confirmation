package com.paulograbin.confirmation.event.usecases.readevent;

import com.paulograbin.confirmation.chapter.Chapter;
import com.paulograbin.confirmation.chapter.ChapterRepository;
import com.paulograbin.confirmation.domain.Event;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.persistence.EventRepository;
import com.paulograbin.confirmation.persistence.UserRepository;
import com.paulograbin.confirmation.persistence.memory.InMemoryChapterRepository;
import com.paulograbin.confirmation.persistence.memory.InMemoryEventRepository;
import com.paulograbin.confirmation.persistence.memory.InMemoryUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReadEventUseCaseTest {

    private ReadEventRequest request;
    private ReadEventResponse response;

    private EventRepository eventRepository;
    private UserRepository userRepository;
    private ChapterRepository chapterRepository;


    @BeforeEach
    void setUp() {
        eventRepository = new InMemoryEventRepository();
        userRepository = new InMemoryUserRepository();
        chapterRepository = new InMemoryChapterRepository();

        request = new ReadEventRequest();
    }

    @AfterEach
    void tearDown() {
        System.out.println(response);
    }

    @Test
    void validRequest() {
        var chapterA = givenAChapter();

        Event event = givenAnEvent(chapterA);
        User user = givenExistingUser(chapterA);

        request.eventId = event.getId();
        request.userId = user.getId();

        whenExecutingTestCase();

        assertThat(response.successful).isTrue();
        assertThat(response.eventDetails.getTitle()).isEqualTo("Title of event");
        assertThat(response.eventDetails.getDescription()).isEqualTo("Description of event");
        assertThat(response.eventDetails.getPublished()).isTrue();
        assertThat(response.eventDetails.getTime()).isNotNull();
        assertThat(response.eventDetails.getDate()).isNotNull();

        assertThat(response.eventDetails.getChapter().getId()).isEqualTo(chapterA.getId());
    }

    @Test
    void validRequestButEventNotPublished() {
        var chapterA = givenAChapter();

        Event event = givenAnEvent(chapterA);
        event.setPublished(false);
        User user = givenExistingUser(chapterA);

        request.eventId = event.getId();
        request.userId = user.getId();

        whenExecutingTestCase();

        assertThat(response.successful).isFalse();
        assertThat(response.notAllowed).isTrue();
    }

    @Test
    void userNotAllowed() {
        var chapterA = givenAChapter();
        var chapterB = givenAnotherChapter();

        Event event = givenAnEvent(chapterA);
        User user = givenExistingUser(chapterB);

        request.eventId = event.getId();
        request.userId = user.getId();

        whenExecutingTestCase();

        assertThat(response.successful).isFalse();
        assertThat(response.notAllowed).isTrue();
    }

    private Chapter givenAnotherChapter() {
        Chapter c = new Chapter();
        c.setName("Another Chapter");
        c.setId(200L);

        chapterRepository.save(c);

        return c;
    }

    private Chapter givenAChapter() {
        Chapter c = new Chapter();
        c.setName("Testing Chapter");
        c.setId(100L);

        chapterRepository.save(c);

        return c;
    }

    private Event givenAnEvent(Chapter c) {
        Event e = new Event();
        e.setTitle("Title of event");
        e.setDescription("Description of event");
        e.setAddress("Address of event");
        e.setPublished(true);
        e.setTime(LocalTime.now());
        e.setDate(LocalDate.now());
        e.setChapter(c);

        eventRepository.save(e);

        return e;
    }

    @Test
    void userDoesntExist() {
        request.userId = 1;
        request.eventId = 1;

        whenExecutingTestCase();

        assertThat(response.successful).isFalse();
        assertThat(response.invalidUser).isTrue();
    }

    private void whenExecutingTestCase() {
        ReadEventUseCase usecase = new ReadEventUseCase(request, eventRepository, userRepository);
        response = usecase.execute();
    }

    @Test
    void eventDoesntExist() {
        givenExistingUser(null);

        request.userId = 1;
        request.eventId = 1;

        whenExecutingTestCase();

        assertThat(response.successful).isFalse();
        assertThat(response.invalidEvent).isTrue();
    }

    private User givenExistingUser(Chapter c) {
        User regularUser = new User();
        regularUser.setId(1L);

        regularUser.setChapter(c);

        userRepository.save(regularUser);

        return regularUser;
    }
}
