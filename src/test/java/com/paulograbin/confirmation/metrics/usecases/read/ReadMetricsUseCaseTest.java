package com.paulograbin.confirmation.metrics.usecases.read;

import com.paulograbin.confirmation.chapter.Chapter;
import com.paulograbin.confirmation.chapter.ChapterRepository;
import com.paulograbin.confirmation.domain.Event;
import com.paulograbin.confirmation.domain.Role;
import com.paulograbin.confirmation.domain.RoleName;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.participation.Participation;
import com.paulograbin.confirmation.participation.ParticipationRepository;
import com.paulograbin.confirmation.participation.ParticipationStatus;
import com.paulograbin.confirmation.persistence.EventRepository;
import com.paulograbin.confirmation.persistence.UserRepository;
import com.paulograbin.confirmation.persistence.memory.InMemoryChapterRepository;
import com.paulograbin.confirmation.persistence.memory.InMemoryEventRepository;
import com.paulograbin.confirmation.persistence.memory.InMemoryParticipationRepository;
import com.paulograbin.confirmation.persistence.memory.InMemoryUserRepository;
import com.paulograbin.confirmation.persistence.memory.InMemoryUserRequestRepository;
import com.paulograbin.confirmation.userequest.UserRequestRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReadMetricsUseCaseTest {

    private static final Long ADMIN_ID = 1L;

    ReadMetricsRequest request;
    ReadMetricsResponse response;

    UserRepository userRepository;
    UserRequestRepository userRequestRepository;
    ChapterRepository chapterRepository;
    EventRepository eventRepository;
    ParticipationRepository participationRepository;

    @BeforeEach
    void setUp() {
        this.userRepository = new InMemoryUserRepository();
        this.userRequestRepository = new InMemoryUserRequestRepository();
        this.chapterRepository = new InMemoryChapterRepository();
        this.eventRepository = new InMemoryEventRepository();
        this.participationRepository = new InMemoryParticipationRepository();
    }

    @AfterEach
    void tearDown() {
        System.out.println(response);
    }

    @Test
    void validRequestExistingAdminUser() {
        givenExistingAdmin();
        givenValidRequest();

        whenExecutingUsecase();

        assertThat(response.successful).isTrue();
    }

    @Test
    void validRequestExistingAdminUserThreeChapters() {
        givenExistingAdmin();
        givenValidRequest();
        givenThreeChapters();

        whenExecutingUsecase();

        assertThat(response.successful).isTrue();
        assertThat(response.totalChapters).isEqualTo(3);
    }

    @Test
    void validRequestExistingAdminUserThreeConfirmations() {
        givenExistingAdmin();
        givenValidRequest();
        givenThreeConfirmations();

        whenExecutingUsecase();

        assertThat(response.successful).isTrue();
        assertThat(response.totalParticipations).isEqualTo(2);
        assertThat(response.confirmedParticipations).isEqualTo(1);
    }

    private void givenThreeConfirmations() {
        Event e1 = new Event();
        e1.setTitle("Event test");
        e1.setDescription("Event test");
        eventRepository.save(e1);

        User a = new User();
        a.setId(50L);
        userRepository.save(a);

        User b = new User();
        a.setId(60L);
        userRepository.save(a);

        Participation p1 = new Participation();
        p1.setEvent(e1);
        p1.setUser(a);
        p1.setStatus(ParticipationStatus.CONVIDADO);

        Participation p2 = new Participation();
        p2.setEvent(e1);
        p2.setUser(b);
        p2.setStatus(ParticipationStatus.CONFIRMADO);

        participationRepository.save(p1);
        participationRepository.save(p2);
    }

    private void givenThreeChapters() {
        Chapter a = new Chapter(100L, "Chapter 100");
        Chapter b = new Chapter(200L, "Chapter 200");
        Chapter c = new Chapter(300L, "Chapter 300");

        chapterRepository.save(a);
        chapterRepository.save(b);
        chapterRepository.save(c);
    }

    @Test
    void validRequestNotAdmin() {
        givenRegularUser();
        givenValidRequest();

        whenExecutingUsecase();

        assertThat(response.successful).isFalse();
        assertThat(response.notAllowed).isTrue();
    }

    private void givenRegularUser() {
        User admin = new User();
        admin.setId(ADMIN_ID);
        admin.getRoles().add(new Role(RoleName.ROLE_USER));

        userRepository.save(admin);
    }

    @Test
    void validRequestNonExistingUser() {
        givenValidRequest();

        whenExecutingUsecase();

        assertThat(response.successful).isFalse();
        assertThat(response.invalidUser).isTrue();
    }

    private void givenExistingAdmin() {
        User admin = new User();
        admin.setId(ADMIN_ID);
        admin.getRoles().add(new Role(RoleName.ROLE_ADMIN));

        userRepository.save(admin);
    }

    private void givenValidRequest() {
        request = new ReadMetricsRequest();

        request.requestingUser = ADMIN_ID;
    }

    private void whenExecutingUsecase() {
        response = new ReadMetricsUseCase(request, userRepository, eventRepository, chapterRepository, userRequestRepository, participationRepository).execute();
    }
}
