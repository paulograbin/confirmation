package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.domain.Chapter;
import com.paulograbin.confirmation.exception.NotFoundException;
import com.paulograbin.confirmation.persistence.ChapterRepository;
import com.paulograbin.confirmation.web.ChapterCreationRequest;
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
        log.info("Fetching chapter {}", chapterId);

        return chapterRepository.findById(chapterId)
                .orElseThrow(() -> new NotFoundException("Chapter " + chapterId + " not found!"));
    }

    public Chapter createChapter(ChapterCreationRequest creationRequest) {
        Chapter chapterToCreate = new Chapter();
        chapterToCreate.setId(creationRequest.getId());
        chapterToCreate.setName(creationRequest.getName());

        validateChapterNumberAvailable(chapterToCreate.getId());

        return chapterRepository.save(chapterToCreate);
    }

    private void validateChapterNumberAvailable(final Long id) {
        if (chapterRepository.existsById(id)) {
            throw new RuntimeException("Chapter id " + id + " is already taken");
        }
    }

    public long count() {
        return chapterRepository.count();
    }
}
