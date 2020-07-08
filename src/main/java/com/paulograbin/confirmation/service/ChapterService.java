package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.domain.Chapter;
import com.paulograbin.confirmation.exception.NotFoundException;
import com.paulograbin.confirmation.persistence.ChapterRepository;
import com.paulograbin.confirmation.usecases.ChapterCreationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class ChapterService {

    private static final Logger log = LoggerFactory.getLogger(ChapterService.class);

    @Resource
    private ChapterRepository chapterRepository;

    public Chapter fetchById(long chapterId) {
        log.info("Fetching chapter by id: {}", chapterId);

        return chapterRepository.findById(chapterId)
                .orElseThrow(() -> new NotFoundException("Chapter " + chapterId + " not found!"));
    }

    public Chapter createChapter(ChapterCreationRequest creationRequest) {
        log.info("Creating new chapter {}", creationRequest.getName());

        Chapter chapterToCreate = new Chapter();
        chapterToCreate.setId(creationRequest.getId());
        chapterToCreate.setName(creationRequest.getName());

        if (isChapterNumberAvailable(chapterToCreate.getId())) {
            throw new RuntimeException("Chapter id " + creationRequest.getId() + " is already taken");
        }

        return chapterRepository.save(chapterToCreate);
    }

    private boolean isChapterNumberAvailable(final Long id) {
        return chapterRepository.existsById(id);
    }

    public long count() {
        return chapterRepository.count();
    }
}
