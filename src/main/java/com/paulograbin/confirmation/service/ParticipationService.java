package com.paulograbin.confirmation.service;


import com.paulograbin.confirmation.Event;
import com.paulograbin.confirmation.Participation;
import com.paulograbin.confirmation.User;
import com.paulograbin.confirmation.persistence.ParticipationRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
}
