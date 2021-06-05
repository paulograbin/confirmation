package com.paulograbin.confirmation.passwordreset.definenewpassword;

import com.paulograbin.confirmation.passwordreset.InMemoryPasswordResetRepository;
import com.paulograbin.confirmation.passwordreset.PasswordRequest;
import com.paulograbin.confirmation.passwordreset.PasswordResetRepository;
import com.paulograbin.confirmation.user.InMemoryUserRepository;
import com.paulograbin.confirmation.user.User;
import com.paulograbin.confirmation.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;


class DefineNewPasswordUseCaseTest {

    private DefineNewPasswordUseCase useCase;
    private DefineNewPasswordRequest request;
    private DefineNewPasswordResponse response;

    private PasswordResetRepository repository;
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        request = new DefineNewPasswordRequest();
        userRepository = new InMemoryUserRepository();
        repository = new InMemoryPasswordResetRepository();

        initMocks(this);
    }

    @Test
    void noPassword() {
        useCase = new DefineNewPasswordUseCase(request, repository, userRepository, passwordEncoder);

        response = useCase.execute();

        assertThat(response.successful).isFalse();
        assertThat(response.invalidRequestCode).isTrue();
        assertThat(response.invalidPassword).isTrue();
    }

    @Test
    void noRequestCode() {
        request.setPassword("aaaaaa");

        useCase = new DefineNewPasswordUseCase(request, repository, userRepository, passwordEncoder);

        response = useCase.execute();

        assertThat(response.successful).isFalse();
        assertThat(response.invalidRequestCode).isTrue();
    }

    @Test
    void invalidCode() {
        request.setRequestCode("aaa");
        request.setPassword("aaaaaa");

        useCase = new DefineNewPasswordUseCase(request, repository, userRepository, passwordEncoder);

        response = useCase.execute();

        assertThat(response.successful).isFalse();
        assertThat(response.invalidRequestCode).isTrue();
    }

    @Test
    void validCodeButNoRequest() {
        UUID uuid = UUID.randomUUID();
        request.setRequestCode(uuid.toString());
        request.setPassword("aaaaaa");

        useCase = new DefineNewPasswordUseCase(request, repository, userRepository, passwordEncoder);

        response = useCase.execute();

        assertThat(response.successful).isFalse();
        assertThat(response.nonExistingRequest).isTrue();
    }

    @Test
    void expiredRequest() {
        UUID uuid = UUID.randomUUID();

        givenExpiredRequest(uuid);

        request.setRequestCode(uuid.toString());
        request.setPassword("aaaaaa");

        useCase = new DefineNewPasswordUseCase(request, repository, userRepository, passwordEncoder);

        response = useCase.execute();

        assertThat(response.successful).isFalse();
        assertThat(response.nonExistingRequest).isTrue();
    }

    @Test
    void validRequestButNoUser() {
        UUID uuid = UUID.randomUUID();

        givenValidRequest(uuid);

        request.setRequestCode(uuid.toString());
        request.setPassword("aaaaaa");

        useCase = new DefineNewPasswordUseCase(request, repository, userRepository, passwordEncoder);

        response = useCase.execute();

        assertThat(response.successful).isFalse();
        assertThat(response.userNotFound).isTrue();
    }

    @Test
    void validRequestWithUser() {
        UUID uuid = UUID.randomUUID();

        givenUser();
        givenValidRequest(uuid);

        request.setRequestCode(uuid.toString());
        request.setPassword("aaaaaa");

        useCase = new DefineNewPasswordUseCase(request, repository, userRepository, passwordEncoder);

        response = useCase.execute();

        assertThat(response.successful).isTrue();
    }

    private void givenUser() {
        User user = new User();
        user.setUsername("tester");
        user.setEmail("teste@teste.com");

        userRepository.save(user);
    }

    private void givenValidRequest(UUID uuid) {
        PasswordRequest passwordRequest = new PasswordRequest();
        passwordRequest.setCode(uuid);
        passwordRequest.setEmailAddress("teste@teste.com");
        passwordRequest.setExpirationDate(LocalDateTime.now().plusMinutes(5));
        passwordRequest.setCreationDate(LocalDateTime.now());

        repository.save(passwordRequest);
    }

    private void givenExpiredRequest(UUID uuid) {
        PasswordRequest passwordRequest = new PasswordRequest();
        passwordRequest.setCode(uuid);
        passwordRequest.setEmailAddress("teste@teste.com");
        passwordRequest.setExpirationDate(LocalDateTime.now().minusMinutes(5));
        passwordRequest.setCreationDate(LocalDateTime.now());

        repository.save(passwordRequest);
    }
}