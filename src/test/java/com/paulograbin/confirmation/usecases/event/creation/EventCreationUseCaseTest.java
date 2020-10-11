package com.paulograbin.confirmation.usecases.event.creation;

import com.paulograbin.confirmation.chapter.Chapter;
import com.paulograbin.confirmation.domain.Participation;
import com.paulograbin.confirmation.domain.ParticipationStatus;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.helper.DateHelper;
import com.paulograbin.confirmation.persistence.EventRepository;
import com.paulograbin.confirmation.persistence.ParticipationRepository;
import com.paulograbin.confirmation.persistence.UserRepository;
import com.paulograbin.confirmation.persistence.memory.InMemoryEventRepository;
import com.paulograbin.confirmation.persistence.memory.InMemoryParticipationRepository;
import com.paulograbin.confirmation.persistence.memory.InMemoryUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


class EventCreationUseCaseTest {

    private static final Long MASTER_ID = 1L;

    private EventRepository repository;
    private ParticipationRepository participationRepository;
    private UserRepository userRepository;

    private EventCreationRequest request;
    private EventCreationResponse response;

    @BeforeEach
    void setUp() {
        repository = new InMemoryEventRepository();
        participationRepository = new InMemoryParticipationRepository();
        userRepository = new InMemoryUserRepository();

        request = makeValidRequest();
        response = new EventCreationResponse();
    }

    @AfterEach
    void tearDown() {
        System.out.println(response);
    }

    private EventCreationRequest makeValidRequest() {
        EventCreationRequest request = new EventCreationRequest();
        request.setTitle("Title");
        request.setDescription("aaaaaa");
        request.setAddress("Title");
        request.setDate(DateHelper.makeFutureDate());
        request.setTime(LocalTime.now());

        request.setCreatorId(MASTER_ID);

        return request;
    }

    @Test
    void valid() {
        givenAMasterUser();
        makeValidRequest();
        whenExecutingTestCase();

        assertThat(response.successful).isTrue();
        assertThat(response.createdEventId).isNotNull();

        Optional<Participation> byEventIdAndUserId = participationRepository.findByEventIdAndUserId(response.createdEventId, request.getCreatorId());
        Participation participation = byEventIdAndUserId.get();

        assertThat(participation.getStatus()).isEqualTo(ParticipationStatus.CONFIRMADO);
    }

    private void whenExecutingTestCase() {
        this.response = new EventCreationUseCase(request, repository, participationRepository, userRepository).execute();
    }

    private void givenAMasterUser() {
        User master = new User();
        master.setId(MASTER_ID);
        master.setMaster(true);

        master.setChapter(new Chapter());

        userRepository.save(master);
    }

    @Test
    void pastDate() {
        givenAMasterUser();
        makeValidRequest();
        whenExecutingTestCase();

        request.setDate(DateHelper.makePastDate());

        whenExecutingTestCase();

        assertThat(response.successful).isFalse();
        assertThat(response.invalidDate).isTrue();
        assertThat(response.createdEventId).isNull();
    }
}
