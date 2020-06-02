package com.paulograbin.confirmation.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class UserTest {

    public static final String USERNAME = "username";
    public static final String FIRST_NAME = "FirstName";
    public static final String LAST_NAME = "Lastname";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "abcabc";

    private User makeUserWithoutDataForTest() {
        return new User();
    }

    private User makeUserWithDataForTest() {
        return new User(USERNAME, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD);
    }

    @Test
    void chapterIsCreatedWithData() {
        User user = makeUserWithDataForTest();

        assertThat(user.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(user.getLastName()).isEqualTo(LAST_NAME);
        assertThat(user.getUsername()).isEqualTo(USERNAME);
        assertThat(user.getEmail()).isEqualTo(EMAIL);
        assertThat(user.getPassword()).isEqualTo(PASSWORD);

        assertThat(user.getRoles()).isEmpty();

        assertThat(user.isMaster()).isFalse();
    }

    @Test
    void chapterIsCreatedWithoutData() {
        User user = makeUserWithoutDataForTest();

        assertThat(user.getFirstName()).isBlank();
        assertThat(user.getLastName()).isBlank();
        assertThat(user.getUsername()).isBlank();
        assertThat(user.getEmail()).isBlank();
        assertThat(user.getPassword()).isBlank();

        assertThat(user.getRoles()).isEmpty();

        assertThat(user.isMaster()).isFalse();
    }

    @Test
    void userIsCreatedWithIdNull() {
        User user = makeUserWithoutDataForTest();

        assertThat(user.getId()).isNull();
    }

    @Test
    void userIsCreatedNotAssignedToAnyChapter() {
        User user = makeUserWithoutDataForTest();

        assertThat(user.getChapters()).isEmpty();
    }

    @Test
    void whenChapterIsAssignedToUser__userIsAssignedToChapter() {
        User user = makeUserWithoutDataForTest();

        Chapter c = new Chapter();
        user.addChapter(c);

        assertThat(user.getChapters()).hasSize(1);
        assertThat(c.getUsers()).hasSize(1);
    }

    @Test
    void userIsCreatedWithoutParticipations() {
        User user = makeUserWithoutDataForTest();

        assertThat(user.getParticipations()).isEmpty();
    }


    @Test
    void userIsCreatedWithoutRoles() {
        User user = makeUserWithoutDataForTest();

        assertThat(user.getRoles()).isEmpty();
    }

    @Test
    void userIsCreatedEnabled() {
        User user = makeUserWithoutDataForTest();

        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void userIsCreatedWithCredentialsNonExpired() {
        User user = makeUserWithoutDataForTest();

        assertThat(user.isCredentialsNonExpired()).isTrue();
    }

    @Test
    void userIsCreatedNonExpired() {
        User user = makeUserWithoutDataForTest();

        assertThat(user.isAccountNonExpired()).isTrue();
    }

    @Test
    void userIsCreatedActive() {
        User user = makeUserWithoutDataForTest();

        assertThat(user.isActive()).isTrue();
    }

    @Test
    void toStringNull() {
        User user = makeUserWithoutDataForTest();

        String toString = user.toString();

        assertThat(toString).contains("id='null'");
        assertThat(toString).contains("username='null'");
    }

    @Test
    void toStringNotNull() {
        User user = makeUserWithDataForTest();

        String toString = user.toString();

        assertThat(toString).contains("id='null'");
        assertThat(toString).contains("username='" + USERNAME + "'");
    }

    @Test
    void justCreatedUserIsNotAdmin() {
        User user = makeUserWithDataForTest();

        assertThat(user.isAdmin()).isFalse();
    }

    @Test
    void justCreatedUserIsNotLocked() {
        User user = makeUserWithDataForTest();

        assertThat(user.isAccountNonLocked()).isTrue();
    }

    @Test
    void userIsCreatedWithoutModificationDate() {
        User user = makeUserWithDataForTest();

        assertThat(user.getModificationDate()).isNull();
    }
    @Test
    void userIsCreatedWithoutInactivationDate() {
        User user = makeUserWithDataForTest();

        assertThat(user.getInactivatedIn()).isNull();
    }
}
