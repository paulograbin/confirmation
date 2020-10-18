package com.paulograbin.confirmation.event.usecases.readevent;

import com.paulograbin.confirmation.domain.Event;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.persistence.EventRepository;
import com.paulograbin.confirmation.persistence.UserRepository;
import com.paulograbin.confirmation.web.dto.EventDetailsDTO;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

public class ReadEventUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ReadEventUseCase.class);

    private final ReadEventRequest request;
    private final ReadEventResponse response;

    private final ModelMapper modelMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;


    public ReadEventUseCase(ReadEventRequest request, EventRepository eventRepository, ModelMapper modelMapper, UserRepository userRepository) {
        this.request = request;
        this.response = new ReadEventResponse();

        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.eventRepository = eventRepository;
    }

    public ReadEventResponse execute() {
        logger.info("Executing with request {}", request);

        if (isValid()) {
            gatherEventData();
        } else {
            setErrors();
        }

        logger.info("Returning response {}", response);
        return response;
    }

    private void gatherEventData() {
        Event event = eventRepository.findById(request.getEventId()).get();
        User user = userRepository.findById(request.getUserId()).get();


        logger.info("Found event {}", event);

        event.setParticipants(event.getParticipants()
                .stream()
                .filter(u -> u.getUser().isActive())
                .collect(Collectors.toSet()));

        response.successful = true;
        response.eventDetails = modelMapper.map(event, EventDetailsDTO.class);
    }

    private boolean isValid() {
        if (!userRepository.existsById(request.userId)) {
            return false;
        }

        if (!eventRepository.existsById(request.eventId)) {
            return false;
        }

        final User user = userRepository.findById(request.userId).get();
        final Event event = eventRepository.findById(request.eventId).get();

        if (!isUserAllowedToSeeEvent(user, event)) {
            return false;
        }

        return true;
    }

    private boolean isUserAllowedToSeeEvent(User user, Event event) {
        return !event.getChapter().getId().equals(user.getChapter().getId());
    }

    private void setErrors() {
        if (!userRepository.existsById(request.userId)) {
            response.invalidUser = true;
            return;
        }

        if (!eventRepository.existsById(request.eventId)) {
            response.invalidEvent = true;
            return;
        }

        final User user = userRepository.findById(request.userId).get();
        final Event event = eventRepository.findById(request.eventId).get();

        if (isUserAllowedToSeeEvent(user, event)) {
            response.errorMessage = "Esse evento pertence a outro capítulo, você não tem acesso a ele.";
            response.notAllowed = true;
        }
    }

}
