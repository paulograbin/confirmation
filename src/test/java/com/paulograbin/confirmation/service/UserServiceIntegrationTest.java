package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.DemoApplication;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.exception.UsernameNotAvailableException;
import com.paulograbin.confirmation.web.H2JpaConfig;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {DemoApplication.class, H2JpaConfig.class})
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@Ignore
public class UserServiceIntegrationTest {

    @Resource
    UserService userService;

    @Test
    public void givenValidUser__whenCreatingUser__shouldBeCreated() {
        User user = makeUser();

        user = userService.createUser(user);

        User returnedUser = userService.fetchById(user.getId());

        assertNotNull(returnedUser);
    }

    @Test(expected = UsernameNotAvailableException.class)
    public void givenAlreadyTakenUsername__whenCreatingUser__shouldThrowException() {
        User user = makeUser();

        userService.createUser(user);

        fail("Should throw exception before reaching this line");
    }

    private User makeUser() {
        User user = new User();

        user.setFirstName("Paulo");
        user.setLastName("Grabin");
        user.setUsername("plgrabin");
        user.setPassword("123");
        return user;
    }
}
