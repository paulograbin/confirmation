package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.exception.NotFoundException;
import com.paulograbin.confirmation.exception.UsernameNotAvailableException;
import com.paulograbin.confirmation.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
public class UserServiceIntegrationTest {

    @Resource
    UserService userService;

    @Resource
    UserRepository userRepository;

    private User user;

    @Test
    public void contextLoads() {
        assertThat(userService).isNotNull();
        assertThat(userRepository).isNotNull();
    }

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        user = null;
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

        User returnedUser = userService.fetchById(user.getId());

        assertThat(returnedUser).isNotNull();
        assertThat(returnedUser.getId()).isNotNull();
    }

    @Test
    public void givenValidId_whenFindingUserById_mustReturnUser() {
        givenUser();

        User returnedUser = userService.fetchById(user.getId());

        assertThat(returnedUser).isNotNull();
    }

    @Test
    public void givenInvalidId_whenFindingUserById_mustThrowException() {
        Throwable throwable = catchThrowable(() -> userService.fetchById(323232L));

        assertThat(throwable).isInstanceOf(NotFoundException.class);
    }


    @Test
    public void givenAlreadyTakenUsername__whenCreatingUser__shouldThrowException() {
        user = makeUser();

        userService.createUser(user);

        assertThrows(UsernameNotAvailableException.class, () -> userService.createUser(user));
    }

    private void givenUser() {
        user = makeUser();
        insertUserIntoDatabase(user);
    }

    private void insertUserIntoDatabase(User user) {
        userService.createUser(user);
    }

    private User makeUser() {
        int randomNumber = new Random().nextInt(1000);

        User user = new User();

        user.setFirstName("Paulo" + randomNumber);
        user.setLastName("Grabin" + randomNumber);
        user.setUsername("plgrabin" + randomNumber);
        user.setEmail("plgrabin@gmail.com" + randomNumber);
        user.setPassword("123" + randomNumber);
        return user;
    }

}
