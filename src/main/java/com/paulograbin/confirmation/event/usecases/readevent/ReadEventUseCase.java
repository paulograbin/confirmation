package com.paulograbin.confirmation.event.usecases.readevent;

import com.paulograbin.confirmation.domain.Event;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.persistence.UserRepository;
import com.paulograbin.confirmation.service.EventService;
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
    private final EventService eventService;
    private final UserRepository userRepository;


    public ReadEventUseCase(ReadEventRequest request, EventService eventService, ModelMapper modelMapper, UserRepository userRepository) {
        this.request = request;
        this.response = new ReadEventResponse();

        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.eventService = eventService;
    }

    public ReadEventResponse execute() {
        logger.info("Executing with request {}", request);

        Event event = eventService.fetchById(request.getEventId());
        User user = userRepository.findById(request.getUserId()).get();

        if (!event.getChapter().getId().equals(user.getChapter().getId())) {
            response.errorMessage = "Esse evento pertence a outro capítulo, você não tem acesso a ele.";
            response.notAllowed = true;

            return response;
        }

        logger.info("Found event {}", event);

        event.setParticipants(event.getParticipants()
                .stream()
                .filter(u -> u.getUser().isActive())
                .collect(Collectors.toSet()));

        response.successful = true;
        response.eventDetails = modelMapper.map(event, EventDetailsDTO.class);

//        if (isValid()) {
//
//        } else {
//            setErrors();
//        }

        logger.info("Returning response {}", response);
        return response;
    }

//    private boolean isValid() {
//        if ()
//    }
}
