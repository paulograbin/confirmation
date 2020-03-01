package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.domain.Event;
import com.paulograbin.confirmation.domain.Participation;
import com.paulograbin.confirmation.domain.ParticipationStatus;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.persistence.ParticipationRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ParticipationService {

    private static final Logger log = LoggerFactory.getLogger(ParticipationService.class);

    @Resource
    ParticipationRepository participationRepository;

    @Resource
    UserService userService;

    @Resource
    EventService eventService;

    @Resource
    private ModelMapper modelMapper;

    public Iterable<Participation> fetchAllParticipations() {
        log.info("Fetching every participation");

        return participationRepository.findAll();
    }


    public List<Participation> getAllParticipationsFromEvent(final long eventId) {
        log.info(String.format("Fetching every from event %d", eventId));

        Event eventFromDatabase = eventService.fetchById(eventId);

        return eventFromDatabase.getParticipants();
    }

    public List<Participation> getAllParticipationsFromUser(final long userId) {
        log.info("Fetching every participation from user {}", userId);
        User userFromDatabase = userService.fetchById(userId);

        List<Participation> userParticipations = userFromDatabase.getParticipations();
        return userParticipations.stream()
                .filter(p -> p.getEvent().isPublished())
                .collect(Collectors.toList());
    }

    public Participation createNew(Event event, User user) {
        Participation p = new Participation(user, event);
        p.setStatus(ParticipationStatus.INVITED);

        return participationRepository.save(p);
    }

    public Optional<Participation> fetchByEventAndUser(long eventId, long userId) {
        return participationRepository.findByEventIdAndUserId(eventId, userId);
    }

    public Participation confirmParticipation(Participation participation) {
        participation.setStatus(ParticipationStatus.CONFIRMED);
        participation.setConfirmationDate(LocalDateTime.now());

        return participationRepository.save(participation);
    }

    public Participation declineParticipation(Participation participation) {
        participation.setStatus(ParticipationStatus.DECLINED);
        participation.setConfirmationDate(LocalDateTime.now());

        return participationRepository.save(participation);
    }

    public void deleteAllParticipationsFromEvent(Long eventId) {
        List<Participation> allParticipationsFromEvent = getAllParticipationsFromEvent(eventId);

        participationRepository.deleteAll(allParticipationsFromEvent);
    }

    public long fetchCount() {
        return participationRepository.count();
    }
}
