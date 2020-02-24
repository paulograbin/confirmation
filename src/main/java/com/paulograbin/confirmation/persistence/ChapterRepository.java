package com.paulograbin.confirmation.persistence;

import com.paulograbin.confirmation.domain.Chapter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ChapterRepository extends CrudRepository<Chapter, Long> {
}
