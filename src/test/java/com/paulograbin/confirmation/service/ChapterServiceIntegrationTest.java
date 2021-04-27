package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.chapter.Chapter;
import com.paulograbin.confirmation.chapter.ChapterService;
import com.paulograbin.confirmation.event.Event;
import com.paulograbin.confirmation.user.User;
import com.paulograbin.confirmation.chapter.ChapterRepository;
import com.paulograbin.confirmation.event.EventService;
import com.paulograbin.confirmation.usecases.ChapterCreationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional
class ChapterServiceIntegrationTest {

    @Resource
    ChapterService chapterService;

    @Resource
    UserService userService;

    @Resource
    EventService eventService;

    @Resource
    ChapterRepository chapterRepository;

    private Chapter chapter;


    @Test
    public void contextLoads() {
        assertThat(chapterService).isNotNull();
        assertThat(chapterRepository).isNotNull();
    }

    @Test
    public void duringCreation_userReceivesId() {
        ChapterCreationRequest request = new ChapterCreationRequest(101L, "Chapter Test");

        chapter = chapterService.createChapter(request);

        assertThat(chapter).isNotNull();
        assertThat(chapter.getId()).isNotNull();
    }

    @Test
    public void givenValidUser__whenCreatingUser__shouldBeCreated() {
        ChapterCreationRequest request = new ChapterCreationRequest(105L, "Chapter Test");

        chapter = chapterService.createChapter(request);

        assertThat(chapter).isNotNull();
        assertThat(chapter.getId()).isNotNull();
    }

    @Test
    public void givenValidId_whenFindingUserById_mustReturnUser() {
        ChapterCreationRequest request = new ChapterCreationRequest(55L, "Chapter Test");
        chapterService.createChapter(request);

        Chapter chapter = chapterService.fetchById(55L);

        assertThat(chapter).isNotNull();
    }

    @Test
    void chapterNumberNotAvailable() {
        ChapterCreationRequest request = new ChapterCreationRequest(56L, "Chapter Test");
        chapterService.createChapter(request);

        assertThrows(RuntimeException.class,
                () -> chapterService.createChapter(request));
    }

    @Test
    void chapterCreatedHasNoEvent() {
        ChapterCreationRequest request = new ChapterCreationRequest(105L, "Chapter Test");

        chapter = chapterService.createChapter(request);

        assertThat(chapter.getEvents()).isEmpty();
    }

    @Test
    void chapterHasEventAfterEventIsCreated() {
        ChapterCreationRequest request = new ChapterCreationRequest(105L, "Chapter Test");
        chapter = chapterService.createChapter(request);

        Event event = makeEvent();
        event = eventService.createEvent(event, event.getCreator());

        assertThat(event.getChapter().getId()).isEqualTo(request.getId());
        assertThat(chapter.getEvents()).hasSize(1);
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

        user.setChapter(chapter);

        userService.createUser(user);

        return user;
    }
}
