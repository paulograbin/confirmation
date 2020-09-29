package com.paulograbin.confirmation.persistence;

import com.paulograbin.confirmation.domain.Event;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface EventRepository extends EntityRepository<Event, Long> {

    List<Event> findAllByCreatorId(long creatorId);

    List<Event> findAllByChapterIdAndDateGreaterThanEqual(final long chapterId, LocalDate moment);

    List<Event> findAllByChapterId(long chapterId);

    long countAllByPublishedTrue();

    long countAllByDateAfter(LocalDate currentDate);
}
