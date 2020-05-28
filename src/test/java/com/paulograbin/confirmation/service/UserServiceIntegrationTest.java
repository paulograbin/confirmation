package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.persistence.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceIntegrationTest {

    @Resource
    UserService userService;

    @Resource
    UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void contextLoads() {
        assertThat(userService).isNotNull();
    }

    @Test
    public void givenValidUser__whenCreatingUser__shouldBeCreated() {
        User user = makeUser();

        user = userService.createUser(user);

        User returnedUser = userService.fetchById(user.getId());

        assertThat(returnedUser).isNotNull();
    }

//    @Test(expected = UsernameNotAvailableException.class)
//    public void givenAlreadyTakenUsername__whenCreatingUser__shouldThrowException() {
//        User user = makeUser();
//
//        userService.createUser(user);
//        userService.createUser(user);
//
//        fail("Should throw exception before reaching this line");
//    }

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
