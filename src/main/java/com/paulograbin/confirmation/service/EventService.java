package com.paulograbin.confirmation.service;


import com.paulograbin.confirmation.domain.Event;
import com.paulograbin.confirmation.domain.Participation;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.exception.NotFoundException;
import com.paulograbin.confirmation.exception.NotYourEventException;
import com.paulograbin.confirmation.exception.UserAlreadyInvitedException;
import com.paulograbin.confirmation.exception.UserNotInvitedException;
import com.paulograbin.confirmation.persistence.EventRepository;
import com.paulograbin.confirmation.security.jwt.CurrentUser;
import com.paulograbin.confirmation.web.EventCreationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    @Resource
    EventRepository eventRepository;

    @Resource
    UserService userService;

    @Resource
    ParticipationService participationService;

    public Iterable<Event> fetchAllEvents() {
        log.info("Fetching all events");

        return eventRepository.findAll();
    }

    public Event createEvent(EventCreationRequest request, User currentUser) {
        Event e = new Event();
        e.setTitle(request.getTitle());
        e.setDescription(request.getDescription());
        e.setAddress(request.getAddress());
        e.setDate(request.getDate());
        e.setTime(request.getTime());

        return this.createEvent(e, currentUser);
    }

    public Event createEvent(Event event, User eventCreator) {
        checkValid(event);

        event.setId(null);
        event.setCreationDate(LocalDateTime.now());
        event.setCreator(eventCreator);

        Event save = eventRepository.save(event);

        Participation creatorParticipation = participationService.createNew(event, event.getCreator());
        participationService.confirmParticipation(creatorParticipation);

        return save;
    }

    public Event updateEvent(long eventId, Event event, @CurrentUser User currentUser) {
        Event eventFromDatabase = fetchById(eventId);

        eventFromDatabase.setTitle(event.getTitle());
        eventFromDatabase.setDescription(event.getDescription());
        eventFromDatabase.setAddress(event.getAddress());
        eventFromDatabase.setDate(event.getDate());
        eventFromDatabase.setTime(event.getTime());

        return eventRepository.save(eventFromDatabase);
    }

    private void checkValid(Event event) {
        if (isBlank(event.getTitle()) || event.getTitle().length() < 5) {
            throw new IllegalArgumentException("Título do evento precisa ter pelo menos 5 letras");
        }

        if (isBlank(event.getDescription()) || event.getDescription().length() < 5) {
            throw new IllegalArgumentException("Descrição do evento precisa ter pelo menos 5 letras");
        }

        if (event.getAddress().isEmpty()) {
            throw new IllegalArgumentException("Faltou informar o endereço");
        }

        if (event.getDate() == null) {
            throw new IllegalArgumentException("Faltou informar a data do evento");
        }

        if (event.getTitle() == null) {
            throw new IllegalArgumentException("Faltou informar o horário do evento");
        }
    }

    public List<Participation> fetchParticipantsByEvent(final long eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        Event event = eventOptional.orElseThrow(() -> new NotFoundException(format("Event %s not found", eventId)));

        return event.getParticipants();
    }

    public List<Event> fetchAllEventsCreatedByUser(long userId) {
        return eventRepository.findAllByCreatorId(userId);
    }

    public Event fetchById(long id) {
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event with id " + id + " not found!"));
    }

    public Participation inviteUserForEvent(final long userId, final long eventId) {
        User user = userService.fetchById(userId);
        Event event = fetchById(eventId);
        log.info("Inviting user {} to event {}", userId, eventId);

        Optional<Participation> participation = participationService.fetchByEventAndUser(event.getId(), user.getId());
        if (participation.isPresent()) {
            throw new UserAlreadyInvitedException(format("User %s is already invited for event %s", userId, eventId));
        }

        return participationService.createNew(event, user);
    }

    public Participation confirmParticipation(long userId, long eventId) {
        Optional<Participation> participationOptional = participationService.fetchByEventAndUser(eventId, userId);

        Participation participation = participationOptional.orElseThrow(() -> new UserNotInvitedException(format("Participation not found for user %s in event %s", userId, eventId)));

        return participationService.confirmParticipation(participation);
    }

    public Participation declineParticipation(long userId, long eventId) {
        Optional<Participation> participationOptional = participationService.fetchByEventAndUser(eventId, userId);

        Participation participation = participationOptional.orElseThrow(() -> new UserNotInvitedException(format("Participation not found for user %s in event %s", userId, eventId)));

        return participationService.declineParticipation(participation);
    }

    public void deleteEvent(long eventId, User currentUser) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(format("Event %s not found", eventId)));

        if (isCurrentUserCreatorOfThisEvent(currentUser, event) || currentUser.isAdmin()) {
            List<Participation> allParticipationsFromEvent = participationService.getAllParticipationsFromEvent(event.getId());

            if (isCreatorTheOnlyInvitedUser(allParticipationsFromEvent)) {
                participationService.deleteAllParticipationsFromEvent(event.getId());
                eventRepository.deleteById(event.getId());
            }
        } else {
            throw new NotYourEventException("This event was not created by you!");
        }
    }

    private boolean isCreatorTheOnlyInvitedUser(List<Participation> allParticipationsFromEvent) {
        return allParticipationsFromEvent.size() == 1;
    }

    private boolean isCurrentUserCreatorOfThisEvent(User currentUser, Event event) {
        return event.getCreator().getId().equals(currentUser.getId());
    }

    public List<Event> fetchUpComingEventsFromChapter(long chapterId) {
        LocalDate yesterday = LocalDate.now().minus(1, ChronoUnit.DAYS);

        return eventRepository.findAllByChapterIdAndDateGreaterThanEqual(chapterId, yesterday);
    }

    public List<Event> fetchAllEventsFromChapter(long chapterId) {
        return eventRepository.findAllByChapterId(chapterId);
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
}
