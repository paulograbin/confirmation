package com.paulograbin.confirmation.persistence;

import com.paulograbin.confirmation.Event;
import org.springframework.data.repository.CrudRepository;


public interface EventRepository extends CrudRepository<Event, Long> {
}
