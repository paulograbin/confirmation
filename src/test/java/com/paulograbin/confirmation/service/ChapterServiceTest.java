package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.chapter.Chapter;
import com.paulograbin.confirmation.chapter.ChapterService;
import com.paulograbin.confirmation.exception.NotFoundException;
import com.paulograbin.confirmation.chapter.ChapterRepository;
import com.paulograbin.confirmation.usecases.ChapterCreationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


class ChapterServiceTest {

    @Mock
    ChapterRepository chapterRepository;

    @InjectMocks
    ChapterService chapterService;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void testContextIsStarted() {
        assertThat(chapterRepository).isNotNull();
        assertThat(chapterService).isNotNull();
    }

    @Test
    void givenEmptyDatabase_whenFetchingCount_mustBeZero() {
        long count = chapterService.count();
        assertThat(count).isEqualTo(0);
    }

    @Test
    void givenDatabaseWithFiveUsers_whenFetchingCount_mustBeFive() {
        when(chapterRepository.count())
                .thenReturn(5L);

        long count = chapterService.count();
        assertThat(count).isEqualTo(5);
    }

    @Test()
    void givenNonExistingId__whenFetchingById__mustThrowException() {
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> {
                    chapterService.fetchById(44);
                });
    }

    @Test()
    void givenExistingId__whenFetchingById__mustReturnChapter() {
        when(chapterRepository.findById(44L))
                .thenReturn(Optional.of(new Chapter()));

        Chapter chapter = chapterService.fetchById(44);
        assertThat(chapter).isNotNull();
    }

    @Test
    void givenValidCreationRequest__whenCreatingNewChaper__mustCreateIt() {
        Chapter chapterToReturn = new Chapter();
        chapterToReturn.setId(44L);
        chapterToReturn.setName("Name");

        when(chapterRepository.save(any()))
                .thenReturn(chapterToReturn);

        ChapterCreationRequest request = new ChapterCreationRequest(44L, "Name");

        Chapter createdChapter = chapterService.createChapter(request);

        assertThat(createdChapter).isNotNull();
        assertThat(createdChapter.getId()).isEqualTo(44L);
        assertThat(createdChapter.getName()).isEqualTo("Name");
    }

    @Test
    void givenCreationRequestForExistingId__whenCreatingNewChaper__mustThrowException() {
        when(chapterRepository.existsById(any()))
                .thenReturn(true);

        ChapterCreationRequest request = new ChapterCreationRequest(44L, "Name");

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> {
                    chapterService.createChapter(request);
                });
    }
}
