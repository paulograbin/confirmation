package com.paulograbin.confirmation.participation;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ParticipationRepository extends CrudRepository<Participation, Long> {

    Optional<Participation> findByEventIdAndUserId(long eventId, long userId);

    List<Participation> findByUserId(long userId);

    long countByStatus(ParticipationStatus status);
}
