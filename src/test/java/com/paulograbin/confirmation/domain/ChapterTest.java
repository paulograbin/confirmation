package com.paulograbin.confirmation.domain;

import com.paulograbin.confirmation.chapter.Chapter;
import com.paulograbin.confirmation.user.User;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class ChapterTest {

    static final String CHAPTER_NAME = "Capitulo teste";

    private Chapter makeChapterForTest() {
        Long id = new Random().nextLong();

        return new Chapter(id, CHAPTER_NAME);
    }

    @Test
    void chapterIsCreatedWithoutNameAndWithoutId() {
        Chapter c = new Chapter();

        assertThat(c.getName()).isBlank();
        assertThat(c.getId()).isNull();
    }

    @Test
    void chapterIsCreatedWithName() {
        Chapter c = makeChapterForTest();

        assertThat(c.getName()).isNotBlank();
    }

    @Test
    void chapterIsCreatedWithoutMembers() {
        Chapter c = makeChapterForTest();

        assertThat(c.getUsers()).isEmpty();
    }

    @Test
    void chapterIsCreatedWithoutEvents() {
        Chapter c = makeChapterForTest();

        assertThat(c.getEvents()).isEmpty();
    }

    @Test
    void whenUserIsAssignedToChapter_ChapterIsAssignedToUser() {
        Chapter chapter = makeChapterForTest();

        User aUser = new User();

        chapter.addUser(aUser);

        assertThat(chapter.getUsers()).isNotEmpty();
        assertThat(aUser.getChapter()).isNotNull();
    }

    @Test
    void toStringWithoutId() {
        Chapter chapter = makeChapterForTest();

        String chapterToString = chapter.toString();
        System.out.println(chapterToString);

        assertThat(chapterToString).contains(CHAPTER_NAME);
        assertThat(chapterToString).contains(chapter.getId() + "");
    }

    @Test
    void toStringWithI() {
        Chapter chapter = makeChapterForTest();
        chapter.setId(555L);

        String chapterToString = chapter.toString();
        System.out.println(chapterToString);

        assertThat(chapterToString).contains(CHAPTER_NAME);
        assertThat(chapterToString).contains("555");
    }
}
