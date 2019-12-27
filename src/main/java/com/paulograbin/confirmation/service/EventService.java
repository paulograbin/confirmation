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

    private void validate() {

    }

    public List<Participation> fetchParticipantsByEvent(long eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        Event event = eventOptional.orElseThrow(() -> new NotFoundException("opa"));

        return event.getParticipants();
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

    public Participation invite2(final long userId, final long eventId) {
        User user = userService.fetchById(userId);
        Event event = fetchById(eventId);

        return participationService.createNew(event, user);
    }
}
