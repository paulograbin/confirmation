package com.paulograbin.confirmation.persistence.memory;

import com.paulograbin.confirmation.event.Event;
import com.paulograbin.confirmation.persistence.EventRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryEventRepository extends InMemoryRepository<Event> implements EventRepository {

    @Override
    public List<Event> findAllByCreatorId(long creatorId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Event> findAllByChapterIdAndDateGreaterThanEqual(long chapterId, LocalDate moment) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Event> findAllByChapterId(long chapterId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long countAllByPublishedTrue() {
        return this.map.values()
                .stream()
                .filter(Event::isPublished)
                .count();
    }

    @Override
    public long countAllByDateAfter(LocalDate currentDate) {
        return this.map.values()
                .stream()
                .filter(e -> e.getDate().isAfter(currentDate))
                .count();
    }

    @Override
    public List<Event> findAllByChapterIdAndDate(long chapterId, LocalDate requestParsedDate) {
        return this.map.values()
                .stream()
                .filter(e -> e.getChapter().getId().equals(chapterId))
                .filter(e -> e.getDate().equals(requestParsedDate))
                .collect(Collectors.toList());
    }

}
