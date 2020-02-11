package com.paulograbin.confirmation.web;

import com.paulograbin.confirmation.Event;
import com.paulograbin.confirmation.User;
import com.paulograbin.confirmation.security.jwt.CurrentUser;
import com.paulograbin.confirmation.service.EventService;
import com.paulograbin.confirmation.service.ParticipationService;
import com.paulograbin.confirmation.web.dto.EventDTO;
import com.paulograbin.confirmation.web.dto.EventDetailsDTO;
import com.paulograbin.confirmation.web.dto.ParticipationDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;


@RestController
public class EventsController {

    private static final Logger log = LoggerFactory.getLogger(EventsController.class);

    @Resource
    private EventService eventService;

    @Resource
    private ParticipationService participationService;

    @Resource
    private ModelMapper modelMapper;


    @GetMapping(path = "/events")
    public List<EventDTO> listAllEvents() {
        log.info("All events");

        Iterable<Event> eventIterator = eventService.fetchAllEvents();
        List<Event> arrayList = Lists.from(eventIterator.iterator());

        return arrayList.stream()
                .map(u -> modelMapper.map(u, EventDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/events/{id}")
    public EventDetailsDTO fetchEventDetails(@PathVariable("id") long eventId) {
        log.info("All details of event {}", eventId);

        Event event = eventService.fetchById(eventId);

        return modelMapper.map(event, EventDetailsDTO.class);
    }

    @GetMapping(path = "/events/createdBy/{userId}")
    public List<EventDetailsDTO> fetchEventsCreatedByUser(@PathVariable("userId") long userId) {
        log.info("Fetching all events created by user {}", userId);

        List<Event> events = eventService.fetchAllEventsCreatedByUser(userId);

        return events.stream()
                .map(e -> modelMapper.map(e, EventDetailsDTO.class))
                .collect(Collectors.toList());
    }

    @PostMapping(path = "/event/{eventId}/invite/{userId}")
    public void inviteParticipant(@PathVariable("eventId") final long eventId, @PathVariable("userId") final long userId) {
        log.info(format("Inviting user %s to event %s", userId, eventId));

        eventService.invite2(userId, eventId);
    }

    @PostMapping(path = "/event/{eventId}/confirm/{userId}")
    public void confirmParticipation(@PathVariable("eventId") final long eventId, @PathVariable("userId") final long userId) {
        log.info("Confirming user {} to event {}", userId, eventId);

        eventService.confirmParticipation(userId, eventId);
    }

    @PostMapping(path = "/event/{eventId}/decline/{userId}")
    public void declineParticipation(@PathVariable("eventId") final long eventId, @PathVariable("userId") final long userId) {
        log.info("Declining user {} to event {}", userId, eventId);

        eventService.declineParticipation(userId, eventId);
    }

    @GetMapping(path = "/events/invitations/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ParticipationDTO> fetchEventsUserIsInvited(@PathVariable("userId") final long userId) {
        log.info("Looking for events to which user {} is invited to", userId);

        return participationService.getAllParticipationsFromUser(userId);
    }

    @PostMapping(path = "/events")
    @ResponseStatus(HttpStatus.CREATED)
    public Event createNewEvent(@RequestBody Event event, @CurrentUser User currentUser) {
        return eventService.createEvent(event, currentUser);
    }
}
