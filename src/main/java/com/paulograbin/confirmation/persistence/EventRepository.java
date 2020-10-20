package com.paulograbin.confirmation.persistence;

import com.paulograbin.confirmation.event.Event;

import java.time.LocalDate;
import java.util.List;


public interface EventRepository extends EntityRepository<Event, Long> {

    List<Event> findAllByCreatorId(long creatorId);

    List<Event> findAllByChapterIdAndDateGreaterThanEqual(final long chapterId, LocalDate moment);

    List<Event> findAllByChapterId(long chapterId);

    long countAllByPublishedTrue();

    long countAllByDateAfter(LocalDate currentDate);

    List<Event> findAllByChapterIdAndDate(long chapterId, LocalDate requestParsedDate);
}
