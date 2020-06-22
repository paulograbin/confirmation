package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.domain.Chapter;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.persistence.ChapterRepository;
import com.paulograbin.confirmation.usecases.ChapterCreationRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class ChapterServiceIntegrationTest {

    @Resource
    ChapterService chapterService;


    @Resource
    ChapterRepository chapterRepository;


    @Test
    public void contextLoads() {
        assertThat(chapterService).isNotNull();
        assertThat(chapterRepository).isNotNull();
    }


    @Test
    public void duringCreation_userReceivesId() {
        ChapterCreationRequest request = new ChapterCreationRequest(100L, "Chapter Test");

        Chapter chapter = chapterService.createChapter(request);

        assertThat(chapter).isNotNull();
        assertThat(chapter.getId()).isNotNull();
    }

    @Test
    public void givenValidUser__whenCreatingUser__shouldBeCreated() {
        ChapterCreationRequest request = new ChapterCreationRequest(105L, "Chapter Test");
        Chapter createdChapter = chapterService.createChapter(request);

        assertThat(createdChapter).isNotNull();
        assertThat(createdChapter.getId()).isNotNull();
    }

    @Test
    public void givenValidId_whenFindingUserById_mustReturnUser() {
        ChapterCreationRequest request = new ChapterCreationRequest(55L, "Chapter Test");
        chapterService.createChapter(request);

        Chapter returnedChapter = chapterService.fetchById(55L);

        assertThat(returnedChapter).isNotNull();
    }

    @Test
    void name2() {
        ChapterCreationRequest request = new ChapterCreationRequest(56L, "Chapter Test");
        chapterService.createChapter(request);

        Assertions.assertThrows(RuntimeException.class, () -> {
            chapterService.createChapter(request);
        });
    }
}
