package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.chapter.Chapter;
import com.paulograbin.confirmation.chapter.ChapterService;
import com.paulograbin.confirmation.domain.Role;
import com.paulograbin.confirmation.domain.RoleName;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.exception.NotFoundException;
import com.paulograbin.confirmation.exception.UsernameNotAvailableException;
import com.paulograbin.confirmation.persistence.UserRepository;
import com.paulograbin.confirmation.usecases.ChapterCreationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceIntegrationTest.class);

    @Resource
    UserService service;

    @Resource
    UserRepository repository;

    @Resource
    ChapterService chapterService;

    private User user;

    
    @BeforeEach
    public void setUp() {
        user = null;
    }
    
    @Test
    public void contextLoads() {
        assertThat(service).isNotNull();
        assertThat(repository).isNotNull();
    }

    @Test
    void assignUserToChapter() {
        givenUser();

        ChapterCreationRequest request = new ChapterCreationRequest(666L, "Chapter for test");
        Chapter chapter = chapterService.createChapter(request);

        user = service.assignUserToChapter(user.getId(), chapter.getId());

        assertThat(user.getChapter()).isNotNull();
        assertThat(chapter.getUsers()).hasSize(1);
    }

    @Test
    void userIsCreatedWithUserRole() {
        givenUser();

        assertThat(user.getRoles()).hasSize(1);

        Role next = user.getRoles().iterator().next();
        assertThat(next.getName()).isEqualByComparingTo(RoleName.ROLE_USER);
    }

    @Test
    void userIsCreatedActive() {
        givenUser();

        assertThat(user.isActive()).isTrue();
    }

    @Test
    public void duringCreation_userReceivesId() {
        givenUser();

        assertThat(user.getId()).isNotNull();
        assertThat(user.getCreationDate()).isNotNull();
        assertThat(user.getRoles()).isNotEmpty();
    }

    @Test
    void recentlyCreatedUserHasNoParticipations() {
        givenUser();

        assertThat(user.getParticipations()).hasSize(0);
    }

    @Test
    public void givenValidUser__whenCreatingUser__shouldBeCreated() {
        givenUser();

        User returnedUser = service.fetchById(user.getId());

        assertThat(returnedUser).isNotNull();
        assertThat(returnedUser.getId()).isNotNull();
    }

    @Test
    public void givenValidId_whenFindingUserById_mustReturnUser() {
        givenUser();

        User returnedUser = service.fetchById(user.getId());

        assertThat(returnedUser).isNotNull();
    }

    @Test
    public void givenInvalidId_whenFindingUserById_mustThrowException() {
        Throwable throwable = catchThrowable(() -> service.fetchById(323232L));

        assertThat(throwable).isInstanceOf(NotFoundException.class);
    }


    @Test
    public void givenAlreadyTakenUsername__whenCreatingUser__shouldThrowException() {
        user = makeUser();

        service.createUser(user);

        assertThrows(UsernameNotAvailableException.class, () -> service.createUser(user));
    }

    @Test
    void inactivate() {
        givenUser();

        service.inactivate(user.getId());

        assertThat(user.isActive()).isFalse();
    }

    @Test
    void activeInactiveUser() {
        givenUser();

        service.inactivate(user.getId());
        service.activate(user.getId());

        assertThat(user.isActive()).isTrue();
    }

    @Test
    void activeActiveUser() {
        givenUser();

        service.activate(user.getId());

        assertThat(user.isActive()).isTrue();
    }

    private void givenUser() {
        user = makeUser();
        insertUserIntoDatabase(user);
    }

    private void insertUserIntoDatabase(User user) {
        service.createUser(user);
    }

    private User makeUser() {
        int randomNumber = new Random().nextInt(1000);

        User user = new User();

        user.setFirstName("Paulo" + randomNumber);
        user.setLastName("Grabin" + randomNumber);
        user.setUsername("generic" + randomNumber);
        user.setEmail("blabla@gmail.com" + randomNumber);
        user.setPassword("123" + randomNumber);
        return user;
    }

}
