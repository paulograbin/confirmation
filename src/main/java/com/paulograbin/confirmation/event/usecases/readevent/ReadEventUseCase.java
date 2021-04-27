package com.paulograbin.confirmation.event.usecases.readevent;

import com.paulograbin.confirmation.user.User;
import com.paulograbin.confirmation.event.Event;
import com.paulograbin.confirmation.event.repository.EventRepository;
import com.paulograbin.confirmation.participation.Participation;
import com.paulograbin.confirmation.participation.ParticipationStatus;
import com.paulograbin.confirmation.user.UserRepository;
import com.paulograbin.confirmation.web.dto.EventDetailsDTO;
import com.paulograbin.confirmation.web.dto.ParticipationWithoutEventDTO;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ReadEventUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ReadEventUseCase.class);

    private final ReadEventRequest request;
    private final ReadEventResponse response;

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    ModelMapper modelMapper = new ModelMapper();


    public ReadEventUseCase(ReadEventRequest request, EventRepository eventRepository, UserRepository userRepository) {
        this.request = request;
        this.response = new ReadEventResponse();

        this.userRepository = userRepository;
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
        if (request.getEventId() == 0) {
            response.creating = true;
            response.successful = true;
            return;
        }

        Event event = eventRepository.findById(request.getEventId()).get();
        User user = userRepository.findById(request.getUserId()).get();

        logger.info("Found event {}", event);
        logger.info("Event has {} responses", event.getParticipants().size());
        List<User> allActiveMembers = userRepository.findAllByChapterIdAndActiveTrue(event.getChapter().getId());
        Map<Long, Participation> allParticipations = new HashMap<>(allActiveMembers.size());
        logger.info("Chapter has {} active members", allActiveMembers.size());

        for (User allMember : allActiveMembers) {
            Participation p = new Participation();
            p.setUser(allMember);
            p.setEvent(event);
            p.setStatus(ParticipationStatus.CONVIDADO);

            allParticipations.put(allMember.getId(), p);
        }

        Set<Participation> participants = event.getParticipants()
                .stream()
                .filter(p -> p.getUser().isActive())
                .collect(Collectors.toSet());
        for (Participation participant : participants) {
            Long userId = participant.getUser().getId();
            Participation participation = allParticipations.get(userId);

            if (participation != null) {
                participation.setStatus(participant.getStatus());
            } else {
                logger.info("Ignoring {} since his active status is {}", participant.getUser().getUsername(), participant.getUser().isActive());
            }
        }

        response.successful = true;
        response.canChange = user.isMaster();
        response.isInThePast = event.getDate().isBefore(LocalDate.now());

        response.eventDetails = modelMapper.map(event, EventDetailsDTO.class);
        response.eventDetails.setParticipants(allParticipations.values().stream().map(p -> modelMapper.map(p, ParticipationWithoutEventDTO.class)).collect(Collectors.toList()));
    }

    private boolean isValid() {
        if (request.getEventId() == 0) {
            return true;
        }

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

        if (!event.isPublished()) {
            return false;
        }

        return true;
    }

    private boolean isUserAllowedToSeeEvent(User user, Event event) {
        return event.getChapter().getId().equals(user.getChapter().getId()) || user.isAdmin();
    }

    private void setErrors() {
        if (!userRepository.existsById(request.userId)) {
            response.invalidUser = true;
            return;
        }

        if (!eventRepository.existsById(request.eventId)) {
            response.errorMessage = "Evento não existente.";
            response.invalidEvent = true;
            return;
        }

        final User user = userRepository.findById(request.userId).get();
        final Event event = eventRepository.findById(request.eventId).get();

        if (!isUserAllowedToSeeEvent(user, event)) {
            response.errorMessage = "Esse evento pertence a outro capítulo, você não tem acesso a ele.";
            response.notAllowed = true;
        }

        if (!event.isPublished()) {
            response.errorMessage = "Esse evento não foi publicado.";
            response.notAllowed = true;
        }
    }

}
