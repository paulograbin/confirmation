package com.paulograbin.confirmation.persistence.memory;

import com.paulograbin.confirmation.chapter.Chapter;
import com.paulograbin.confirmation.chapter.ChapterRepository;
import com.paulograbin.confirmation.domain.Event;
import com.paulograbin.confirmation.persistence.EventRepository;

import java.time.LocalDate;
import java.util.List;

public class InMemoryChapterRepository extends InMemoryRepository<Chapter> implements ChapterRepository {


}
