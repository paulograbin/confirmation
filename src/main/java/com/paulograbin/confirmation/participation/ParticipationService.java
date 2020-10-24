package com.paulograbin.confirmation.participation;

import com.paulograbin.confirmation.DateUtils;
import com.paulograbin.confirmation.event.Event;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.exception.NotFoundException;
import com.paulograbin.confirmation.event.EventService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class ParticipationService {

    private static final Logger log = LoggerFactory.getLogger(ParticipationService.class);

    @Resource
    ParticipationRepository participationRepository;

    @Resource
    EventService eventService;

    @Resource
    private ModelMapper modelMapper;

    public Iterable<Participation> fetchAllParticipations() {
        log.info("Fetching every participation");

        return participationRepository.findAll();
    }

    public Set<Participation> getAllParticipationsFromEvent(final long eventId) {
        log.info(String.format("Fetching every participation from event %d", eventId));

        Event eventFromDatabase = eventService.fetchById(eventId);

        return eventFromDatabase.getParticipants();
    }

    public List<Participation> getAllUpcomingParticipationsFromUser(final long userId) {
        log.info("Fetching every up coming participation from user {}", userId);

        final LocalDate yesterday = DateUtils.getCurrentDate()
                .toLocalDate()
                .minus(1, ChronoUnit.DAYS);

        return getAllParticipationsFromUser(userId).stream()
                .filter(p -> p.getEvent().getDate().isAfter(yesterday))
                .collect(Collectors.toList());
    }


    public List<Participation> getAllParticipationsFromUser(final long userId) {
        log.info("Fetching every participation from user {}", userId);

        return participationRepository.findByUserId(userId)
                .stream()
                .filter(p -> p.getEvent().isPublished())
                .collect(Collectors.toList());
    }

    public Participation createNew(Event event, User user) {
        log.info("Inviting user {}-{} to event {}-{}", user.getId(), user.getUsername(), event.getId(), event.getTitle());

        Participation p = new Participation(user, event);
        p.inviteParticipant();

        return participationRepository.save(p);
    }

    public Participation fetchByEventAndUser(long eventId, long userId) {
        return participationRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " is not invited to " + eventId));
    }

    public Participation confirmParticipation(Participation participation) {
        participation.confirmParticipant();
        participation.setConfirmationDate(DateUtils.getCurrentDate());

        return participationRepository.save(participation);
    }

    public Participation declineParticipation(Participation participation) {
        participation.declineParticipant();
        participation.setConfirmationDate(DateUtils.getCurrentDate());

        return participationRepository.save(participation);
    }

    public void deleteAllParticipationsFromEvent(Long eventId) {
        log.info("Removing participations...");

        Set<Participation> allParticipationsFromEvent = getAllParticipationsFromEvent(eventId);
        log.info("Found {} participations for event {}", allParticipationsFromEvent.size(), eventId);

        allParticipationsFromEvent.stream()
                .peek(System.out::println)
                .forEach(p -> participationRepository.delete(p));

        Set<Participation> afterDeletion = getAllParticipationsFromEvent(eventId);
        log.info("After deletion there are {} participations for event {}", afterDeletion.size(), eventId);
    }

    public long fetchCount() {
        return participationRepository.count();
    }
}
