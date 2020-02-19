package com.paulograbin.confirmation.service;


import com.paulograbin.confirmation.Event;
import com.paulograbin.confirmation.Participation;
import com.paulograbin.confirmation.User;
import com.paulograbin.confirmation.exception.NotFoundException;
import com.paulograbin.confirmation.persistence.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        log.info("Service - fetch all");

        return eventRepository.findAll();
    }

    public Event createEvent(Event event) {
        validate();

        event.setId(null);
        event.setCreationDate(LocalDateTime.now());
        return eventRepository.save(event);
    }

    private boolean isValid(Event event) {
        if (event.getTitle().length() < 5) {
            throw new IllegalArgumentException();
        }

        if (event.getAddress().isEmpty()) {
            throw new IllegalArgumentException();
        }

        if (event.getDateTime() == null) {
            throw new IllegalArgumentException();
        }

        return true;
    }

    public List<Participation> fetchParticipantsByEvent(final long eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        Event event = eventOptional.orElseThrow(() -> new EventNotFoundException(format("Event %s not found", eventId)));

        return event.getParticipants();
    }

    public List<Event> fetchAllEventsCreatedByUser(long userId) {
        return eventRepository.findAllByCreatorId(userId);
    }

    public Event fetchById(long id) {
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event with id " + id + " not found!"));
    }

    public void inviteUserForEvent(final long userId, final long eventId) {
        User user = userService.fetchById(userId);
        Event event = fetchById(eventId);

        event.addParticipant(user);

        eventRepository.save(event);
    }

    public Participation declineParticipation(long userId, long eventId) {
        Optional<Participation> participationOptional = participationService.fetchByEventAndUser(eventId, userId);

        Participation participation = participationOptional.orElseThrow(() -> new UserNotInvitedException(format("Participation not found for user %s in event %s", userId, eventId)));

        return participationService.declineParticipation(participation);
    }

    public void deleteEvent(long eventId, User currentUser) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(format("Event %s not found", eventId)));

        if (isCurrentUserCreatorOfThisEvent(currentUser, event)) {
            List<Participation> allParticipationsFromEvent = participationService.getAllParticipationsFromEvent(event.getId());

            if (isCreatorTheOnlyInvitedUser(allParticipationsFromEvent)) {
                participationService.deleteParticipations(allParticipationsFromEvent);
                eventRepository.deleteById(event.getId());
            }
        }
    }

    private boolean isCreatorTheOnlyInvitedUser(List<Participation> allParticipationsFromEvent) {
        return allParticipationsFromEvent.size() == 1;
    }

    private boolean isCurrentUserCreatorOfThisEvent(User currentUser, Event event) {
        return event.getCreator().getId().equals(currentUser.getId());
    }
}
