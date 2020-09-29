package com.paulograbin.confirmation.chapter;

import com.paulograbin.confirmation.chapter.Chapter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ChapterRepository extends CrudRepository<Chapter, Long> {
}
