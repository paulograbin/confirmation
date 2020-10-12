package com.paulograbin.confirmation.userequest.usecases.creation;

import com.paulograbin.confirmation.chapter.Chapter;
import com.paulograbin.confirmation.chapter.ChapterRepository;
import com.paulograbin.confirmation.domain.Role;
import com.paulograbin.confirmation.domain.RoleName;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.persistence.UserRepository;
import com.paulograbin.confirmation.persistence.memory.InMemoryChapterRepository;
import com.paulograbin.confirmation.persistence.memory.InMemoryUserRepository;
import com.paulograbin.confirmation.persistence.memory.InMemoryUserRequestRepository;
import com.paulograbin.confirmation.userequest.UserRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class CreatePseudoUserUseCaseTest {

    private UserRepository userRepository;
    private UserRequestRepository userRequestRepository;
    private ChapterRepository chapterRepository;

    private CreatePseudoUserRequest request;
    private CreatePseudoUserResponse response;

    private final Long ADMIN_ID = 1L;
    private final Long MASTER_ID = 2L;

    @BeforeEach
    void setUp() {
        request = new CreatePseudoUserRequest();

        userRequestRepository = new InMemoryUserRequestRepository();
        userRepository = new InMemoryUserRepository();
        chapterRepository = new InMemoryChapterRepository();
    }

    private void whenExecutingUseCase() {
        response = new CreatePseudoUserUseCase(request, userRepository, userRequestRepository, chapterRepository).execute();
    }

    private void makeValidRequest() {
        request.requestingUser = ADMIN_ID;
        request.email = "plgrabin@gmail.com";
        request.firstName = "Paulo";
        request.lastName = "Grabin";
        request.chapterId = 592L;
    }

    @Test
    void nonExistingRequestingUser() {
        givenAnAdminUserExists();
        givenExistingChapter();
        makeValidRequest();

        request.requestingUser = 763L;

        whenExecutingUseCase();

        assertThat(response.successful).isFalse();
        assertThat(response.invalidRequestingUser).isTrue();
    }

    @Test
    void valid() {
        givenAnAdminUserExists();
        givenExistingChapter();
        makeValidRequest();

        whenExecutingUseCase();

        assertThat(response.successful).isTrue();
    }

    @Test
    void emailAlreadyUsed() {
        givenAnAdminUserExists();
        givenExistingChapter();
        makeValidRequest();

        whenExecutingUseCase();
        whenExecutingUseCase();

        assertThat(response.successful).isFalse();
        assertThat(response.emailNotAvailable).isTrue();
    }

    @Test
    void nonExistingChapter() {
        givenAnAdminUserExists();
        makeValidRequest();

        whenExecutingUseCase();

        assertThat(response.successful).isFalse();
        assertThat(response.invalidChapter).isTrue();
    }

    @Test
    void invalidFirstNameBecauseOfNull() {
        givenAnAdminUserExists();
        givenExistingChapter();
        makeValidRequest();

        request.firstName = null;

        whenExecutingUseCase();

        assertThat(response.successful).isFalse();
        assertThat(response.invalidFirstName).isTrue();
    }

    @Test
    void invalidFirstNameNotEnoughLength() {
        givenAnAdminUserExists();
        givenExistingChapter();
        makeValidRequest();

        request.firstName = "ab";

        whenExecutingUseCase();

        assertThat(response.successful).isFalse();
        assertThat(response.invalidFirstName).isTrue();
    }

    @Test
    void invalidLastNameBecauseOfNull() {
        givenAnAdminUserExists();
        givenExistingChapter();
        makeValidRequest();

        request.lastName = null;

        whenExecutingUseCase();

        assertThat(response.successful).isFalse();
        assertThat(response.invalidLastName).isTrue();
    }

    @Test
    void invalidLastNameNotEnoughLength() {
        givenAnAdminUserExists();
        givenExistingChapter();
        makeValidRequest();

        request.lastName = "ab";

        whenExecutingUseCase();

        assertThat(response.successful).isFalse();
        assertThat(response.invalidLastName).isTrue();
    }

    @Test
    void invalidEmail() {
        givenAnAdminUserExists();
        givenExistingChapter();
        makeValidRequest();

        request.email = "ab@dadadad222";

        whenExecutingUseCase();

        assertThat(response.successful).isFalse();
        assertThat(response.invalidEmail).isTrue();
    }

    private void givenExistingChapter() {
        Chapter chapter = new Chapter();
        chapter.setId(592L);
        chapter.setName("Test Chapter");

        chapterRepository.save(chapter);
    }

    @Test
    void requestingUserNotAdminOrMaster() {
        givenExistingChapter();
        makeValidRequest();

        User regularUser = new User();
        regularUser.setMaster(false);
        regularUser.setEmail("regularuser@confirmation.com");
        regularUser.setRoles(Collections.emptySet());
        userRepository.save(regularUser);

        whenExecutingUseCase();

        assertThat(response.notAllowed).isTrue();
        assertThat(response.successful).isFalse();
    }

    @Test
    void masterNotAdmin() {
        givenExistingChapter();
        givenAnMasterUser();
        makeValidRequest();
        request.requestingUser = 2;

        whenExecutingUseCase();

        assertThat(response.successful).isTrue();
    }

    @Test
    void name() {
        givenAnAdminUserExists();

        whenExecutingUseCase();

        assertThat(response.invalidFirstName).isTrue();
        assertThat(response.invalidLastName).isTrue();
        assertThat(response.successful).isFalse();
    }

    @Test
    void nameExceedMaxLength() {
        givenAnAdminUserExists();
        givenExistingChapter();
        makeValidRequest();
        request.firstName = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        whenExecutingUseCase();

        assertThat(response.invalidFirstName).isTrue();
        assertThat(response.successful).isFalse();
    }

    @Test
    void lastNameExceedMaxLength() {
        givenAnAdminUserExists();
        givenExistingChapter();
        makeValidRequest();
        request.lastName = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        whenExecutingUseCase();

        assertThat(response.invalidLastName).isTrue();
        assertThat(response.successful).isFalse();
    }

    private void givenAnMasterUser() {
        User master = new User();
        master.setId(MASTER_ID);
        master.setEmail("master@confirmation");
        master.setMaster(true);

        userRepository.save(master);
    }

    private void givenAnAdminUserExists() {
        User admin = new User();
        admin.setId(ADMIN_ID);
        admin.getRoles().add(new Role(RoleName.ROLE_ADMIN));
        admin.setEmail("admin@confirmation.com");

        userRepository.save(admin);
    }
}
