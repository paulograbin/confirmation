package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.chapter.Chapter;
import com.paulograbin.confirmation.chapter.ChapterService;
import com.paulograbin.confirmation.domain.Event;
import com.paulograbin.confirmation.participation.Participation;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.participation.ParticipationService;
import com.paulograbin.confirmation.usecases.ChapterCreationRequest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Disabled
public class EndToEndTest {

    @Resource
    ChapterService chapterService;

    @Resource
    UserService userService;

    @Resource
    EventService eventService;

    @Resource
    ParticipationService participationService;

    @Test
    @Transactional
    void name() {
        ChapterCreationRequest chapterRequest = new ChapterCreationRequest(100L, "Chapter 1");
        Chapter chapterOne = chapterService.createChapter(chapterRequest);
        assertThat(chapterOne.getEvents()).hasSize(0);

        User chapterOneMaster = new User();
        chapterOneMaster.setEmail("master@chapterOne.com");
        chapterOneMaster.setUsername("masterchapterone");
        chapterOneMaster.setPassword("aaaaaa");
        chapterOneMaster = userService.createUser(chapterOneMaster);

        User chapterOneFirstInvited = new User();
        chapterOneFirstInvited.setEmail("firstinvited@chapterOne.com");
        chapterOneFirstInvited.setUsername("firstinvitedchapterone");
        chapterOneFirstInvited.setPassword("aaaaaa");
        chapterOneFirstInvited = userService.createUser(chapterOneFirstInvited);

        User chapterOneSecondInvited = new User();
        chapterOneSecondInvited.setEmail("secondinvited@chapterOne.com");
        chapterOneSecondInvited.setUsername("secondinvitedchapterone");
        chapterOneSecondInvited.setPassword("aaaaaa");
        chapterOneSecondInvited = userService.createUser(chapterOneSecondInvited);

        chapterOneMaster = userService.assignUserToChapter(chapterOneMaster.getId(), chapterOne.getId());
        chapterOneFirstInvited = userService.assignUserToChapter(chapterOneFirstInvited.getId(), chapterOne.getId());
        chapterOneSecondInvited = userService.assignUserToChapter(chapterOneSecondInvited.getId(), chapterOne.getId());

        Event e1 = new Event();
        e1.setTitle("Teste 1");
        e1.setDescription("Evento teste");
        e1.setAddress("Address test");
        e1.setDate(LocalDate.now().plus(1, ChronoUnit.DAYS));
        e1.setTime(LocalTime.now());
        e1.setPublished(true);
        e1.setCreator(chapterOneMaster);

        e1 = eventService.createEvent(e1, chapterOneMaster);

        chapterOne = chapterService.fetchById(chapterOne.getId());
        assertThat(chapterOne.getEvents()).hasSize(1);

        Participation userMasterEventOne = participationService.fetchByEventAndUser(e1.getId(), chapterOneMaster.getId());
        Participation userFirstEventOne = participationService.fetchByEventAndUser(e1.getId(), chapterOneFirstInvited.getId());
        Participation userSecondEventOne = participationService.fetchByEventAndUser(e1.getId(), chapterOneSecondInvited.getId());

        assertThat(userMasterEventOne.getUser().getUsername()).isEqualTo(chapterOneMaster.getUsername());
        assertThat(userFirstEventOne.getUser().getUsername()).isEqualTo(chapterOneFirstInvited.getUsername());
        assertThat(userSecondEventOne.getUser().getUsername()).isEqualTo(chapterOneSecondInvited.getUsername());

        e1 = eventService.fetchById(e1.getId());
        assertThat(e1.getParticipants()).hasSize(3);
    }
}
