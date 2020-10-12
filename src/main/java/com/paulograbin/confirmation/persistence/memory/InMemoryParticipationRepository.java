package com.paulograbin.confirmation.persistence.memory;

import com.paulograbin.confirmation.participation.Participation;
import com.paulograbin.confirmation.participation.ParticipationRepository;
import com.paulograbin.confirmation.participation.ParticipationStatus;

import java.util.List;
import java.util.Optional;

public class InMemoryParticipationRepository extends InMemoryRepository<Participation> implements ParticipationRepository {

    @Override
    public Optional<Participation> findByEventIdAndUserId(long eventId, long userId) {
        return map.values()
                .stream()
                .filter(p -> p.getEvent().getId() == eventId)
                .filter(p -> p.getUser().getId() == userId)
                .findFirst();
    }

    @Override
    public List<Participation> findByUserId(long userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long countByStatus(ParticipationStatus status) {
        return map.values()
                .stream()
                .filter(p -> p.getStatus() == status)
                .count();
    }

//    @Override
//    public long countByStatusConfirmado() {
//        return map.values()
//                .stream()
//                .filter(p -> p.getStatus() == ParticipationStatus.CONFIRMADO)
//                .count();
//    }
}
