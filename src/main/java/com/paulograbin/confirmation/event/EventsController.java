package com.paulograbin.confirmation.event;

import com.google.gson.Gson;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.event.usecases.readevent.ReadEventRequest;
import com.paulograbin.confirmation.event.usecases.readevent.ReadEventResponse;
import com.paulograbin.confirmation.event.usecases.readevent.ReadEventUseCase;
import com.paulograbin.confirmation.participation.Participation;
import com.paulograbin.confirmation.participation.ParticipationService;
import com.paulograbin.confirmation.event.repository.EventRepository;
import com.paulograbin.confirmation.participation.ParticipationStatus;
import com.paulograbin.confirmation.persistence.UserRepository;
import com.paulograbin.confirmation.security.jwt.CurrentUser;
import com.paulograbin.confirmation.event.usecases.creation.EventCreationRequest;
import com.paulograbin.confirmation.event.usecases.creation.EventCreationResponse;
import com.paulograbin.confirmation.web.dto.EventDTO;
import com.paulograbin.confirmation.web.dto.EventDetailsDTO;
import com.paulograbin.confirmation.web.dto.ParticipationWithoutUserDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.net.URI;
import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;


@CrossOrigin("*")
@RestController
@RequestMapping(value = "/events")
class EventsController {

    private static final Logger log = LoggerFactory.getLogger(EventsController.class);

    @Resource
    private EventService eventService;

    @Resource
    private EventRepository eventRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private ParticipationService participationService;

    @Resource
    private ModelMapper modelMapper;


    @GetMapping
    public ResponseEntity<List<EventDTO>> listAllEvents() {
        log.info("Listing all events");

        Iterable<Event> eventIterator = eventService.fetchAllEvents();
        List<Event> arrayList = Lists.from(eventIterator.iterator());

        List<EventDTO> collect = arrayList.stream()
                .map(u -> modelMapper.map(u, EventDTO.class))
                .collect(Collectors.toList());

        CacheControl cc = CacheControl.maxAge(Duration.ofMinutes(10)).cachePrivate();
        return ResponseEntity.ok()
                .cacheControl(cc)
                .body(collect);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ReadEventResponse> fetchEventDetails(@PathVariable("id") long eventId, @CurrentUser User currentUser) {
        log.info("All details of event {} for user {}-{}", eventId, currentUser.getId(), currentUser.getUsername());

        var request = new ReadEventRequest();
        request.setEventId(eventId);
        request.setUserId(currentUser.getId());

        var response = new ReadEventUseCase(request, eventRepository, userRepository).execute();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/chapter")
    public List<EventDetailsDTO> fetchEventsFromChapter(@CurrentUser User currentUser) {
        log.info("Fetching all events from chapter {}", currentUser.getChapter().getId());

        // todo: remove participants from the DTO, not needed where its used

        return eventService.fetchUpComingEventsFromChapter(currentUser.getChapter().getId())
                .stream()
                .sorted(Comparator.comparing(Event::getDate))
                .map(e -> modelMapper.map(e, EventDetailsDTO.class))
                .collect(Collectors.toList());
    }

    @PostMapping(path = "/{eventId}/invite/{userId}")
    public void inviteParticipant(@PathVariable("eventId") final long eventId, @PathVariable("userId") final long userId) {
        log.info(format("Inviting user %s to event %s", userId, eventId));

        eventService.inviteUserForEvent(userId, eventId);
    }

    @PostMapping(path = "/{eventId}/confirm/{userId}")
    public void confirmParticipation(@PathVariable("eventId") final long eventId, @PathVariable("userId") final long userId) {
        log.info("Confirming user {} to event {}", userId, eventId);

        eventService.confirmParticipation(userId, eventId);
    }

    @PostMapping(path = "/{eventId}/decline/{userId}")
    public void declineParticipation(@PathVariable("eventId") final long eventId, @PathVariable("userId") final long userId) {
        log.info("Declining user {} to event {}", userId, eventId);

        eventService.declineParticipation(userId, eventId);
    }

    @PostMapping(path = "/{eventId}/confirm")
    public void confirmParticipationNew(@PathVariable("eventId") final long eventId, @CurrentUser User currentUser) {
        log.info("Confirming user {} to event {}", currentUser.getId(), eventId);

        eventService.confirmParticipation(currentUser.getId(), eventId);
    }

    @PostMapping(path = "/{eventId}/decline")
    public void declineParticipationNew(@PathVariable("eventId") final long eventId, @CurrentUser User currentUser) {
        log.info("Declining user {} to event {}", currentUser.getId(), eventId);

        eventService.declineParticipation(currentUser.getId(), eventId);
    }

//    @GetMapping(path = "/invitations/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public List<ParticipationWithoutUserDTO> fetchUpcomingEventsUserIsInvited(@PathVariable("userId") final long userId) {
//        log.info("Looking for events to which user {} is invited to", userId);
//
//        return participationService.getAllUpcomingParticipationsFromUser(userId).stream()
//                .map(p -> modelMapper.map(p, ParticipationWithoutUserDTO.class))
//                .collect(Collectors.toList());
//    }
//
    @GetMapping(path = "/invitations", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ParticipationWithoutUserDTO> fetchUpcomingEventsUserIsInvited(@CurrentUser User currentUser) {
        log.info("Looking for events to which user {} is invited to", currentUser.getId());

        List<Participation> allUpcomingParticipationsFromUser = participationService.getAllUpcomingParticipationsFromUser(currentUser.getId());
        List<Event> upcomingEvents = eventService.fetchUpComingEventsFromChapter(currentUser.getChapter().getId());
        log.info("Found {} upcoming events",  upcomingEvents.size());
        log.info("User has {} participations",  allUpcomingParticipationsFromUser.size());

        Map<Long, Participation> participationMap = new HashMap<>();
        for (Event upcomingEvent : upcomingEvents) {
            Participation p = new Participation();
            p.setEvent(upcomingEvent);
            p.setUser(currentUser);
            p.setStatus(ParticipationStatus.CONVIDADO);

            participationMap.put(upcomingEvent.getId(), p);
        }

        for (Participation participation : allUpcomingParticipationsFromUser) {
            Participation participation1 = participationMap.get(participation.getEvent().getId());
            participation1.setStatus(participation.getStatus());
        }

        return participationMap.values().stream()
                .map(p -> modelMapper.map(p, ParticipationWithoutUserDTO.class))
                .sorted(Comparator.comparing(p -> p.getEvent().getDate()))
                .collect(Collectors.toList());
    }

    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createNewEvent(@RequestBody EventCreationRequest eventToCreate, @CurrentUser User currentUser) {
        EventCreationResponse response = eventService.createEvent(eventToCreate, currentUser);

        if (response.successful) {
            return ResponseEntity.created(URI.create("event/" + response.createdEventId))
                    .body(new Gson().toJson(response));
        } else {
            return ResponseEntity.badRequest().body(new Gson().toJson(response));
        }
    }

    @PutMapping(path = "/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDetailsDTO updateEvent(@PathVariable("eventId") long eventId, @RequestBody Event event, @CurrentUser User currentUser) {
        log.info("Received message to update event {}", eventId);

        Event updatedEvent = eventService.updateEvent(eventId, event, currentUser);

        return modelMapper.map(updatedEvent, EventDetailsDTO.class);
    }

    @PutMapping(path = "/{eventId}/publish")
    @ResponseStatus(HttpStatus.OK)
    public EventDetailsDTO publishEvent(@PathVariable("eventId") long eventId, @CurrentUser User currentUser) {
        Event publishedEvent = eventService.publishEvent(eventId, currentUser);

        return modelMapper.map(publishedEvent, EventDetailsDTO.class);
    }

    @DeleteMapping(path = "/{eventId}")
    public ResponseEntity<Object> deleteEvent(@PathVariable("eventId") final long eventId, @CurrentUser User currentUser) {
        eventService.deleteEvent(eventId, currentUser);

        return ResponseEntity.ok().build();
    }
}
