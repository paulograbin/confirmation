package com.paulograbin.confirmation.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.persistence.UserRepository;
import com.paulograbin.confirmation.service.UserService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


//@RunWith(MockitoJUnitRunner.class)
@Disabled
public class UsersControllerUnitTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserService userService;

    @InjectMocks
    UsersController usersController;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(usersController)
                .build();
    }

    @Test
    public void listNoUsers_WhenThereAreNone() throws Exception {
        when(userService.fetchAll()).thenReturn(Lists.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void listOneUser__whenTheresOneUser() throws Exception {
        User userToReturn = new User("username", "firstname", "lastname", "email", "aaa");
        userToReturn.setId(666l);

        when(userService.fetchAll()).thenReturn(Lists.list(userToReturn));

        User next = userService.fetchAll().iterator().next();

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is("username")))
                .andExpect(jsonPath("$[0].firstName", is("firstname")))
                .andExpect(jsonPath("$[0].lastName", is("lastname")))
                .andExpect(jsonPath("$[0].id", is(666)));
    }

//    @Test
//    public void afterCreation_userIsReturned() throws Exception {
//        SignUpRequest request = new SignUpRequest();
//        request.setEmail("email");
//        request.setUsername("user");
//        request.setFirstName("first name");
//        request.setLastName("last name");
//        request.setPassword("password");
//
//        Gson gson = new Gson();
//        String signupRequestJson = gson.toJson(request);
//
//        when(userRepository.save(any())).thenReturn(userToCreate);
//        when(userService.createUser(any())).thenCallRealMethod();
//
//        ResultActions resultActions = mockMvc.perform(
//                post("/users")
//                        .characterEncoding("UTF-8")
//                        .content(signupRequestJson)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated());
//
//        mockMvc.perform(get("/users"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].username", is("username")))
//                .andExpect(jsonPath("$[0].firstName", is("firstname")))
//                .andExpect(jsonPath("$[0].lastName", is("lastname")));
//    }
}
