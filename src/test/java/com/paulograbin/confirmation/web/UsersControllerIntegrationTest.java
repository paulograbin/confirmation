package com.paulograbin.confirmation.web;

import com.paulograbin.confirmation.DemoApplication;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.persistence.UserRepository;
import com.paulograbin.confirmation.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.annotation.Resource;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {DemoApplication.class, H2JpaConfig.class})
//@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@Disabled
public class UsersControllerIntegrationTest {

    @Resource
    UserRepository userRepository;

    @Resource
    UserService userService;

    @Resource
    private MockMvc mockMvc;


    @BeforeEach
    public void setUp() {
        givenThereAreNoUsers();
    }

    @Test
    public void givenThereAreNoUsers__whenFetchingAllUsers__thenListNoUsers() throws Exception {
        givenThereAreNoUsers();

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));

        assertEquals(0, userRepository.count());
    }

    private void givenThereAreNoUsers() {
        userRepository.deleteAll();
        assertEquals(0, userRepository.count());
    }

    @Test
    public void listOneUser__whenTheresOneUser() {
        User userFromDatabase = givenExistingUserOnDatabase();

//        Response s = when()
//                .get("http://localhost:3000/users").
//                        thenReturn();
//
////        andExpect(jsonPath())
////                .andExpect(jsonPath("$[0].firstName", is(userFromDatabase.getFirstName())))
////                .andExpect(jsonPath("$[0].lastName", is(userFromDatabase.getLastName())))
////                .andExpect(jsonPath("$[0].id", is(not(emptyOrNullString()))));
//
//        System.out.println("s " + s + " ************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************ ");
//
//        ResultActions resultActions = mockMvc.perform(get("/users"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
//
//        resultActions.andExpect(jsonPath("$", hasSize(1)))
//                .


        assertEquals(1, userRepository.count());
    }

    private User givenExistingUserOnDatabase() {
        User user = new User();
        user.setUsername("usernameNew");
        user.setFirstName("firstnameNew");
        user.setLastName("lastnameNew");

        return userService.createUser(user);
    }

    @Test
    public void afterCreation_userIsReturned() throws Exception {
        String userJson = "{ \"username\": \"username\", \"firstName\": \"firstname\", \"lastName\": \"lastname\" }";

        ResultActions resultActions = mockMvc.perform(
                post("/users")
                        .characterEncoding("UTF-8")
                        .content(userJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is("username")))
                .andExpect(jsonPath("$[0].firstName", is("firstname")))
                .andExpect(jsonPath("$[0].lastName", is("lastname")));

        assertEquals(1, userRepository.count());
    }

    @Test
    public void afterDeletingUser__mustNotBeActive() throws Exception {
        User userFromDatabase = givenExistingUser();

        assertTrue(userFromDatabase.isActive());

        sendDeleteUserRequest(userFromDatabase.getId());

        mockMvc.perform(get("/users/" + userFromDatabase.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.inactivatedIn", is(not(emptyOrNullString()))))
                .andExpect(jsonPath("$.modificationDate", is(not(emptyOrNullString()))))
                .andExpect(jsonPath("$.active", is(false)))
                .andExpect(jsonPath("$.id", is(not(emptyOrNullString()))));
    }

    private User givenExistingUser() {
        User user = new User();
        user.setUsername("username");
        user.setFirstName("firstname");
        user.setLastName("lastname");

        return userService.createUser(user);
    }

    private void sendDeleteUserRequest(long id) throws Exception {
        mockMvc.perform(delete("/users/" + id))
                .andExpect(status().isOk());
    }

    @Test
    public void afterUpdate_newInfoMustBeReturned() {

    }
}
