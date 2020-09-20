package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.domain.Role;
import com.paulograbin.confirmation.domain.RoleName;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.exception.EmailNotAvailableException;
import com.paulograbin.confirmation.exception.InvalidRequestException;
import com.paulograbin.confirmation.exception.NotFoundException;
import com.paulograbin.confirmation.exception.UsernameNotAvailableException;
import com.paulograbin.confirmation.persistence.UserRepository;
import com.paulograbin.confirmation.service.mail.EmailService;
import com.paulograbin.confirmation.usecases.user.UpdateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleService roleService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    private User makeTestUser() {
        User user = new User();

        user.setEmail("email@email.com");
        user.setUsername("plgrabin");
        user.setPassword("plgrabin");

        return user;
    }

    @Test
    void givenJustCreatedUser__whenFetching__mustHaveNoParticipations() {
        User expectedUser = makeTestUser();
        expectedUser.setId(333L);

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(expectedUser));

        User user = userService.fetchById(333L);

        assertThat(user.getParticipations()).hasSize(0);
    }

    @Test
    void givenCustomer__whenFetchingItByID__mustReturn() {
        User expectedUser = makeTestUser();
        expectedUser.setId(333L);

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(expectedUser));

        User user = userService.fetchById(333L);

        assertThat(user.getId()).isEqualTo(333L);
    }

    @Test
    void givenThreeUsers__whenRegularUserFetchingAll__mustReturnZeroUsers() {
        List<User> expected = new ArrayList<>();

        expected.add(makeTestUser());
        expected.add(makeTestUser());
        expected.add(makeTestUser());

        Mockito.when(userRepository.findAll())
                .thenReturn(expected);

        User regularUser = new User();
        Iterable<User> actualAllUsers = userService.fetchAll(regularUser);

        assertThat(actualAllUsers).hasSize(0);
    }

    @Test
    void givenNoUsers__whenFetchingAll__mustReturnZeroUsers() {
        List<User> expected = new ArrayList<>();

        Mockito.when(userRepository.findAll())
                .thenReturn(expected);

        User adminuser = new User();
        Iterable<User> actualAllUsers = userService.fetchAll(adminuser);

        assertThat(actualAllUsers).hasSize(expected.size());
    }

    @Test
    void givenNoUser__whenFetchingById__mustThrowException() {
        Mockito.when(userRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.fetchById(33L));
    }

    @Test
    void assetDependenciesAreSet() {
        assertThat(userRepository).isNotNull();
        assertThat(userService).isNotNull();
    }

    @Test
    void givenEmptyDatabase__whenFetchingCount__mustBeZero() {
        long count = userService.fetchCount();
        assertThat(count).isEqualTo(0);
    }

    @Test
    void givenDatabaseWithFiveUsers__whenFetchingCount__mustBeFive() {
        when(userRepository.count())
                .thenReturn(5L);

        long count = userService.fetchCount();
        assertThat(count).isEqualTo(5);
    }

//    @Test
//    void inactivate() {
//        User userToReturn = makeTestUser();
//        userToReturn.setId(44L);
//        userToReturn.setUsername("username");
//
//        when(userRepository.findById(44L))
//                .thenReturn(Optional.of(userToReturn));
//
//        User inactivated = userService.inactivate(44L);
//
//        assertThat(inactivated.isActive()).isFalse();
//    }

    @Test()
    void givenCustomerWithoutEmail__whenCreatingUser__mustThrowException() {
        User user = makeTestUser();
        user.setEmail(null);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test()
    void givenCustomerWithoutUsername__whenCreatingUser__mustThrowException() {
        User user = makeTestUser();
        user.setEmail("email@email.com");
        user.setUsername(null);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test()
    void givenCustomerWithoutPassword__whenCreatingUser__mustThrowException() {
        User user = makeTestUser();
        user.setPassword(null);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    void usernameTaken() {
        when(userRepository.existsByUsername(anyString())).thenReturn(Boolean.TRUE);

        User user = makeTestUser();

        assertThrows(UsernameNotAvailableException.class, () -> userService.createUser(user));
    }

    @Test
    void emailTaken() {
        when(userRepository.existsByEmail(anyString())).thenReturn(Boolean.TRUE);

        User user = makeTestUser();

        assertThrows(EmailNotAvailableException.class, () -> userService.createUser(user));
    }

    @Test
    void userJustCreatedHasNecessaryInformation() {
        when(passwordEncoder.encode(anyString())).thenReturn("aaa");
        when(roleService.getUserRole()).thenReturn(new Role(RoleName.ROLE_USER));

        User mockCreatedUser = makeTestUser();
        mockCreatedUser.setId(44L);
        mockCreatedUser.setPassword("aaa");
        mockCreatedUser.setCreationDate(LocalDateTime.now());
        mockCreatedUser.setRoles(Set.of(new Role(RoleName.ROLE_USER)));

        when(userRepository.save(any(User.class))).thenReturn(mockCreatedUser);


        User user = makeTestUser();

        User createdUser = userService.createUser(user);

        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getRoles()).isNotEmpty();
        assertThat(createdUser.getPassword()).isNotBlank();
        assertThat(createdUser.getCreationDate()).isNotNull();
    }

    @Test
    void givenExistingUsername__whenFetchingUserByUsername__mustReturnUser() {
        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.of(makeTestUser()));

        User user = userService.fetchByUsername("plgrabin@gmail.com");
        assertThat(user).isNotNull();
    }

    @Test
    void whenUpdatingNonExistingUser__mustThrowException() {
        assertThrows(NotFoundException.class,
                () -> userService.updateUser(534343L, new UpdateUserRequest()));
    }

    @Test
    void whenUpdatingUserIdMustMatchRequest() {
        User expectedUser = makeTestUser();
        expectedUser.setId(333L);

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(expectedUser));

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setId(55L);

        assertThrows(InvalidRequestException.class,
                () -> userService.updateUser(50L, updateUserRequest));
    }

    @Test
    void whenUpdatingExistingUser__mustSendMail() {
        User expectedUser = makeTestUser();
        expectedUser.setId(333L);

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(expectedUser));


        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setId(333L);

        userService.updateUser(333L, updateRequest);

        verify(emailService, times(1)).sendPasswordChangedMail(any());
    }
}
