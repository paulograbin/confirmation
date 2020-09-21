package com.paulograbin.confirmation.usecases.event.creation;

import com.paulograbin.confirmation.domain.Chapter;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.helper.DateHelper;
import com.paulograbin.confirmation.persistence.EventRepository;
import com.paulograbin.confirmation.persistence.memory.InMemoryEventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;


class EventCreationUseCaseTest {

    private EventRepository repository;
    private EventCreationRequest request;
    private EventCreationResponse response;

    private EventCreationUseCase usecase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryEventRepository();

        request = makeValidRequest();
        response = new EventCreationResponse();

        usecase = new EventCreationUseCase(request, response, repository);
    }

    @AfterEach
    void tearDown() {
        System.out.println(response);
    }

    private EventCreationRequest makeValidRequest() {
        EventCreationRequest.EventCreationRequestBuilder builder = EventCreationRequest.builder();
        builder.withTitle("Title");
        builder.withDescription("aaaaaa");
        builder.withAddress("Title");
        builder.withDate(DateHelper.makeFutureDate());
        builder.withTime(LocalTime.now());
        builder.withCreator(makeCreator());

        return builder.build();
    }

    @Test
    void valid() {
        usecase.execute();

        assertThat(response.successful).isTrue();
        assertThat(response.createdEventId).isNotNull();
    }

    @Test
    void pastDate() {
        request.setDate(DateHelper.makePastDate());

        usecase.execute();

        assertThat(response.successful).isFalse();
        assertThat(response.invalidDate).isTrue();
        assertThat(response.createdEventId).isNull();
    }

    private User makeCreator() {
        Chapter c = new Chapter();

        User user = new User();
        user.setChapter(c);

        return user;
    }
}
