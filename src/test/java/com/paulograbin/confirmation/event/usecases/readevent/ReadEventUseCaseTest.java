package com.paulograbin.confirmation.event.usecases.readevent;

import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.persistence.EventRepository;
import com.paulograbin.confirmation.persistence.UserRepository;
import com.paulograbin.confirmation.persistence.memory.InMemoryEventRepository;
import com.paulograbin.confirmation.persistence.memory.InMemoryUserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReadEventUseCaseTest {

    private ReadEventRequest request;
    private ReadEventResponse response;

    private EventRepository eventRepository;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        eventRepository = new InMemoryEventRepository();
        userRepository = new InMemoryUserRepository();

        request = new ReadEventRequest();
    }

    @AfterEach
    void tearDown() {
        System.out.println(response);
    }

    @Test
    void userDoesntExist() {
        request.userId = 1;
        request.eventId = 1;

        ReadEventUseCase usecase = new ReadEventUseCase(request, eventRepository, null, userRepository);
        response = usecase.execute();

        Assertions.assertThat(response.successful).isFalse();
        Assertions.assertThat(response.invalidUser).isTrue();
    }

    @Test
    void eventDoesntExist() {
        givenExistingUser();

        request.userId = 1;
        request.eventId = 1;

        ReadEventUseCase usecase = new ReadEventUseCase(request, eventRepository, null, userRepository);
        response = usecase.execute();

        Assertions.assertThat(response.successful).isFalse();
        Assertions.assertThat(response.invalidEvent).isTrue();
    }

    private void givenExistingUser() {
        User regularUser = new User();
        regularUser.setId(1L);

        userRepository.save(regularUser);
    }
}
