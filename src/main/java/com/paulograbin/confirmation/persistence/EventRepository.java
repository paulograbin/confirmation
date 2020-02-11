package com.paulograbin.confirmation.persistence;

import com.paulograbin.confirmation.Event;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface EventRepository extends CrudRepository<Event, Long> {

    List<Event> findAllByCreatorId(long creatorId);

}
