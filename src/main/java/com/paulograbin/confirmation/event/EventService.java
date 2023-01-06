package com.paulograbin.confirmation.event;

import com.paulograbin.confirmation.DateUtils;
import com.paulograbin.confirmation.chapter.Chapter;
import com.paulograbin.confirmation.chapter.ChapterRepository;
import com.paulograbin.confirmation.chapter.ChapterService;
import com.paulograbin.confirmation.event.repository.EventRepository;
import com.paulograbin.confirmation.event.usecases.creation.EventCreationRequest;
import com.paulograbin.confirmation.event.usecases.creation.EventCreationResponse;
import com.paulograbin.confirmation.event.usecases.creation.EventCreationUseCase;
import com.paulograbin.confirmation.exception.NotFoundException;
import com.paulograbin.confirmation.exception.NotYourEventException;
import com.paulograbin.confirmation.exception.UserAlreadyInvitedException;
import com.paulograbin.confirmation.participation.Participation;
import com.paulograbin.confirmation.participation.ParticipationRepository;
import com.paulograbin.confirmation.participation.ParticipationService;
import com.paulograbin.confirmation.security.jwt.CurrentUser;
import com.paulograbin.confirmation.service.UserService;
import com.paulograbin.confirmation.service.mail.EmailService;
import com.paulograbin.confirmation.user.User;
import com.paulograbin.confirmation.user.UserRepository;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.lang.String.format;


@Service
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    @Resource
    EventRepository eventRepository;

    @Resource
    UserService userService;

    @Resource
    ChapterService chapterService;

    @Resource
    ChapterRepository chapterRepository;

    @Resource
    ParticipationService participationService;

    @Resource
    ParticipationRepository participationRepository;

    @Resource
    UserRepository userRepository;

    @Resource
    EmailService emailService;

    public Iterable<Event> fetchAllEvents() {
        log.info("Fetching all events");

        return eventRepository.findAll();
    }

    public Event fetchById(long eventId) {
        log.info("Fetching event by id {}", eventId);

        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found!"));
    }

    public EventCreationResponse createEvent(EventCreationRequest request, User currentUser) {
        request.setCreatorId(currentUser.getId());

        EventCreationResponse eventCreationResponse = new EventCreationUseCase(request, eventRepository, participationRepository, userRepository, emailService, chapterRepository).execute();

        return eventCreationResponse;
    }

    @Transactional
    public Event createEvent(Event eventToCreate, User eventCreator) {
        log.info("Creating event {} for user {}", eventToCreate.getTitle(), eventCreator.getUsername());

        eventToCreate.setId(null);
        eventToCreate.setCreationDate(DateUtils.getCurrentDate());
        eventToCreate.setCreator(eventCreator);
        eventToCreate.setPublished(true);
        // TODO fix this
        eventToCreate.setChapter(eventCreator.getChapter());

        Event createdEvent = eventRepository.save(eventToCreate);

        Chapter chapter = eventCreator.getChapter();
        chapter.getEvents().add(createdEvent);
        chapterService.update(chapter);

//        inviteRemainingUsersFromChapterToEvent(createdEvent);

//        Participation creatorParticipation = participationService.fetchByEventAndUser(eventToCreate.getId(), eventToCreate.getCreator().getId());
        Participation creatorParticipation = new Participation(createdEvent.getCreator(), eventToCreate);
        participationRepository.save(creatorParticipation);

        return createdEvent;
    }

    public Event updateEvent(long eventId, Event event, @CurrentUser User currentUser) {
        log.info("Updating event {} for user {}", eventId, currentUser.getUsername());
        Event eventFromDatabase = fetchById(eventId);

        eventFromDatabase.setTitle(event.getTitle());
        eventFromDatabase.setDescription(event.getDescription());
        eventFromDatabase.setAddress(event.getAddress());
        eventFromDatabase.setPublished(event.isPublished());
        eventFromDatabase.setDate(event.getDate());
        eventFromDatabase.setTime(event.getTime());

//        inviteRemainingUsersFromChapterToEvent(eventFromDatabase);

        return eventRepository.save(eventFromDatabase);
    }

    public Participation inviteUserForEvent(final long userId, final long eventId) {
        final User user = userService.fetchById(userId);
        final Event event = fetchById(eventId);
        log.info("Inviting user {} to event {}", userId, eventId);

        Participation participation = participationService.fetchByEventAndUser(event.getId(), user.getId());
        if (participation != null) {
            throw new UserAlreadyInvitedException(format("User %s is already invited for event %s", userId, eventId));
        }

        return participationService.createNew(event, user);
    }

    public Participation confirmParticipation(long userId, long eventId) {
        log.info("Confirming participation for user {} on event {}", userId, eventId);

        Event event = fetchById(eventId);
        User user = userService.fetchById(userId);

        if (event.getDate().isBefore(DateUtils.getCurrentDate().toLocalDate())) {
            throw new RuntimeException("Evento já aconteceu.");
        }

        if (event.getChapter().getId() != user.getChapter().getId()) {
            throw new RuntimeException("Usuário não pertence a esse capítulo");
        }

        return participationService.confirmParticipation(eventId, userId);
    }

    public Participation declineParticipation(long userId, long eventId) {
        log.info("Declining participation for user {} on event {}", userId, eventId);

        Event event = fetchById(eventId);
        User user = userService.fetchById(userId);

        if (event.getDate().isBefore(DateUtils.getCurrentDate().toLocalDate())) {
            throw new RuntimeException("Evento já aconteceu.");
        }

        if (event.getChapter().getId() != user.getChapter().getId()) {
            throw new RuntimeException("Usuário não pertence a esse capítulo");
        }

        return participationService.declineParticipation(eventId, userId);
    }

    public void deleteEvent(long eventId, User currentUser) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(format("Event %s not found", eventId)));

        if (isCurrentUserCreatorOfThisEvent(currentUser, event) || currentUser.isAdmin()) {
            log.info("Proceeding with event deletion....");

            participationService.deleteAllParticipationsFromEvent(event.getId());


            log.info("Deleting event....");
            eventRepository.deleteById(event.getId());
        } else {
            throw new NotYourEventException("This event was not created by you!");
        }

        log.info("Event deleted!");
    }

    private boolean isCurrentUserCreatorOfThisEvent(User currentUser, Event event) {
        return event.getCreator().getId().equals(currentUser.getId());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Event> fetchUpComingEventsFromChapter(long chapterId) {
        LocalDate yesterday = DateUtils.getCurrentDate()
                .toLocalDate()
                .minus(1, ChronoUnit.DAYS);

        return eventRepository.findAllByChapterIdAndDateGreaterThanEqual(chapterId, yesterday);
    }

    public Event publishEvent(long eventId, User currentUser) {
        log.info("Publishing event {} by user {}", eventId, currentUser.getUsername());

        Event event = this.fetchById(eventId);

        if (event.getCreator().getId().equals(currentUser.getId()) || currentUser.isAdmin()) {
            event.setPublished(true);
            this.eventRepository.save(event);

            return event;
        } else {
            throw new NotYourEventException("Event " + eventId + " does not belong to you!");
        }
    }

    public long fetchCount() {
        return eventRepository.count();
    }
}
