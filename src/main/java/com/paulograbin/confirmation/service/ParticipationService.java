package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.domain.Event;
import com.paulograbin.confirmation.domain.Participation;
import com.paulograbin.confirmation.domain.ParticipationStatus;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.persistence.ParticipationRepository;
import com.paulograbin.confirmation.web.dto.ParticipationDTO;
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

    @Resource
    ParticipationRepository participationRepository;

    public Iterable<Participation> teste() {
        return participationRepository.findAll();
    }


    public Participation createNew(Event event, User user) {
        Participation p = new Participation(user, event);

        return participationRepository.save(p);
    }
    public Participation confirmPartitipation(Participation participation) {
        participation.setStatus(ParticipationStatus.CONFIRMED);
        participation.setConfirmationDate(LocalDateTime.now());

        return participationRepository.save(participation);
    }

    public Participation declineParticipation(Participation participation) {
        participation.setStatus(ParticipationStatus.DECLINED);
        participation.setConfirmationDate(LocalDateTime.now());

        return participationRepository.save(participation);
    }
}
