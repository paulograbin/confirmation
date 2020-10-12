package com.paulograbin.confirmation.persistence.memory;

import com.paulograbin.confirmation.domain.Event;
import com.paulograbin.confirmation.persistence.EventRepository;

import java.time.LocalDate;
import java.util.List;

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
        throw new UnsupportedOperationException();
    }

    @Override
    public long countAllByDateAfter(LocalDate currentDate) {
        throw new UnsupportedOperationException();
    }

}
