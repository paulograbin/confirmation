package com.paulograbin.confirmation.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChapterTest {

    @Test
    void chapterIsCreatedWithName() {
        Chapter c = new Chapter("Capitulo teste");

        assertThat(c.getName()).isNotBlank();
    }

    @Test
    void chapterIsCreatedWithoutMembers() {
        Chapter c = new Chapter("Capitulo teste");

        assertThat(c.getUsers()).isEmpty();
    }

    @Test
    void chapterIsCreatedWithoutEvents() {
        Chapter c = new Chapter("Capitulo teste");

        assertThat(c.getEvents()).isEmpty();
    }

    @Test
    void chapterIsCreatedWithoutId() {
        Chapter c = new Chapter("Capitulo teste");

        assertThat(c.getId()).isNull();
    }

    @Test
    void whenUserIsAssignedToChapter_ChapterIsAssignedToUser() {
        Chapter chapter = new Chapter("Capitulo teste");

        User aUser = new User();

        chapter.addUser(aUser);

        assertThat(chapter.getUsers()).isNotEmpty();
        assertThat(aUser.getChapter()).isNotEmpty();
    }
}
