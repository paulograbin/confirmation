package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.exception.NotFoundException;
import com.paulograbin.confirmation.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.MockitoAnnotations.initMocks;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }


    @Test
    void name() {
        User expectedUser = makeTestUser();
        expectedUser.setId(333L);

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(expectedUser));

        User user = userService.fetchById(333L);

        assertThat(user.getId()).isEqualTo(333L);
    }

    @Test
    void givenThreeUsers__whenFetchingAll__mustReturnTreeUsers() {
        List<User> expected = new ArrayList<>();

        expected.add(makeTestUser());
        expected.add(makeTestUser());
        expected.add(makeTestUser());

        Mockito.when(userRepository.findAll())
                .thenReturn(expected);

        Iterable<User> actualAllUsers = userService.fetchAll();

        assertThat(actualAllUsers).hasSize(expected.size());
    }

    @Test
    void givenNoUsers__whenFetchingAll__mustReturnZeroUsers() {
        List<User> expected = new ArrayList<>();

        Mockito.when(userRepository.findAll())
                .thenReturn(expected);

        Iterable<User> actualAllUsers = userService.fetchAll();

        assertThat(actualAllUsers).hasSize(expected.size());
    }


    private User makeTestUser() {
        return new User();
    }

    @Test
    void givenNoUser__whenFetchingById__mustThrowException() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.fetchById(33L));
    }

    @Test
    void assetDependenciesAreSet() {
        assertThat(userRepository).isNotNull();
        assertThat(userService).isNotNull();
    }
}
