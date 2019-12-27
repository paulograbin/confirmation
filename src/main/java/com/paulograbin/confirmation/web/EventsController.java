package com.paulograbin.confirmation.web;

import com.paulograbin.confirmation.Event;
import com.paulograbin.confirmation.Participation;
import com.paulograbin.confirmation.service.EventService;
import com.paulograbin.confirmation.web.dto.EventDTO;
import com.paulograbin.confirmation.web.dto.EventDetailsDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


@RestController
public class EventsController {

    private static final Logger log = LoggerFactory.getLogger(EventsController.class);

    @Resource
    private EventService eventService;

    private ModelMapper modelMapper = new ModelMapper();


    @RequestMapping(path = "/events", method = RequestMethod.GET)
    public List<EventDTO> listAllEvents() {
        log.info("All events");

        Iterable<Event> eventIterator = eventService.fetchAllEvents();
        List<Event> arrayList = Lists.from(eventIterator.iterator());

        return arrayList.stream()
                .map(u -> modelMapper.map(u, EventDTO.class))
                .collect(Collectors.toList());
    }

    @RequestMapping(path = "/events/{id}", method = RequestMethod.GET)
    public EventDetailsDTO fetchEventDetails(@PathVariable("id") long eventId) {
        log.info("All details of event " + eventId);

        Event event = eventService.fetchById(eventId);

        return modelMapper.map(event, EventDetailsDTO.class);
    }

    @RequestMapping(path = "/events/{id}/participants", method = RequestMethod.GET)
    public List<Participation> listParticipants(@PathVariable("id") long eventId) {
        log.info("All participants of event " + eventId);

        return eventService.fetchParticipantsByEvent(eventId);
    }

    @RequestMapping(path = "/events", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Event createNewEvent(@RequestBody Event event) {
        log.info(event.toString());

        return eventService.createEvent(event);
    }
}
