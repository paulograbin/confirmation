package com.paulograbin.confirmation.persistence;

import com.paulograbin.confirmation.Participation;
import org.springframework.data.repository.CrudRepository;


public interface ParticipationRepository extends CrudRepository<Participation, Long> {
}
