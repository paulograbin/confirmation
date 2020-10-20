package com.paulograbin.confirmation.usecases.event.creation;

import com.paulograbin.confirmation.chapter.Chapter;
import com.paulograbin.confirmation.chapter.ChapterRepository;
import com.paulograbin.confirmation.domain.Event;
import com.paulograbin.confirmation.participation.Participation;
import com.paulograbin.confirmation.participation.ParticipationStatus;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.helper.DateHelper;
import com.paulograbin.confirmation.persistence.EventRepository;
import com.paulograbin.confirmation.participation.ParticipationRepository;
import com.paulograbin.confirmation.persistence.UserRepository;
import com.paulograbin.confirmation.persistence.memory.InMemoryChapterRepository;
import com.paulograbin.confirmation.persistence.memory.InMemoryEventRepository;
import com.paulograbin.confirmation.persistence.memory.InMemoryParticipationRepository;
import com.paulograbin.confirmation.persistence.memory.InMemoryUserRepository;
import com.paulograbin.confirmation.service.mail.EmailService;
import com.paulograbin.confirmation.service.mail.LoggerEmailService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


class EventCreationUseCaseTest {

    private static final Long MASTER_ID = 1L;

    private static final String VALID_TITLE = "aaaaaaaaaabbbbbbbbbbcccccccccc11111111112222222222aaaaaaaaaabbbbbbbbbbcccccccccc11111111112222222222aaaaaaaaaabbbbbbbbbbcccccccccc11111111112222222222aaaaaaaaaabbbbbbbbbbcccccccccc11111111112222222222aaaaaaaaaabbbbbbbbbbcccccccccc11111111112222222222";
    private static final String INVALID_TITLE = "aaaaaaaaaabbbbbbbbbbcccccccccc11111111112222222222aaaaaaaaaabbbbbbbbbbcccccccccc11111111112222222222aaaaaaaaaabbbbbbbbbbcccccccccc11111111112222222222aaaaaaaaaabbbbbbbbbbcccccccccc11111111112222222222aaaaaaaaaabbbbbbbbbbcccccccccc11111111112222222222999999999";


    private ChapterRepository chapterRepository;
    private EventRepository repository;
    private ParticipationRepository participationRepository;
    private UserRepository userRepository;

    private EmailService emailService;

    private EventCreationRequest request;
    private EventCreationResponse response;

    @BeforeEach
    void setUp() {
        repository = new InMemoryEventRepository();
        participationRepository = new InMemoryParticipationRepository();
        userRepository = new InMemoryUserRepository();
        emailService = new LoggerEmailService();
        chapterRepository = new InMemoryChapterRepository();

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

    @Test
    void duplicatedEvent() {
        givenAMasterUser();
        makeValidRequest();
        whenExecutingTestCase();
        whenExecutingTestCase();

        assertThat(response.successful).isFalse();
        assertThat(response.duplicated).isTrue();
        assertThat(response.createdEventId).isNull();
    }

    @Test
    void validRequestWithTitleAboveLength() {
        givenAMasterUser();
        makeValidRequest();
        request.setTitle(INVALID_TITLE);
        whenExecutingTestCase();

        assertThat(response.successful).isTrue();
        assertThat(response.createdEventId).isNotNull();

        Event event = repository.findById(response.createdEventId).get();
        assertThat(event.getTitle()).isEqualToIgnoringCase(VALID_TITLE);

        Optional<Participation> byEventIdAndUserId = participationRepository.findByEventIdAndUserId(response.createdEventId, request.getCreatorId());
        Participation participation = byEventIdAndUserId.get();

        assertThat(participation.getStatus()).isEqualTo(ParticipationStatus.CONFIRMADO);
    }

    private void whenExecutingTestCase() {
        this.response = new EventCreationUseCase(request, repository, participationRepository, userRepository, emailService, chapterRepository).execute();
    }

    private void givenAMasterUser() {
        User master = new User();
        master.setId(MASTER_ID);
        master.setFirstName("MasterFirstName");
        master.setMaster(true);

        Chapter chapter = new Chapter();
        chapter.setId(100L);
        chapter.setName("Testing Chapter");
        chapterRepository.save(chapter);

        master.setChapter(chapter);

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


    @Test
    void invalidTitle() {
        givenAMasterUser();
        makeValidRequest();
        request.setTitle("a");

        whenExecutingTestCase();

        assertThat(response.successful).isFalse();
        assertThat(response.invalidTitle).isTrue();
    }

    @Test
    void invalidDescription() {
        givenAMasterUser();
        makeValidRequest();
        request.setDescription("a");

        whenExecutingTestCase();

        assertThat(response.successful).isFalse();
        assertThat(response.invalidDescription).isTrue();
    }

    @Test
    void invalidAddress() {
        givenAMasterUser();
        makeValidRequest();
        request.setAddress("");

        whenExecutingTestCase();

        assertThat(response.successful).isFalse();
        assertThat(response.invalidAddress).isTrue();
    }

    @Test
    void invalidDate() {
        givenAMasterUser();
        makeValidRequest();
        request.setDate("");

        whenExecutingTestCase();

        assertThat(response.successful).isFalse();
        assertThat(response.invalidDate).isTrue();
    }

    @Test
    void invalidTime() {
        givenAMasterUser();
        makeValidRequest();
        request.setTime(null);

        whenExecutingTestCase();

        assertThat(response.successful).isFalse();
        assertThat(response.invalidTime).isTrue();
    }
}
