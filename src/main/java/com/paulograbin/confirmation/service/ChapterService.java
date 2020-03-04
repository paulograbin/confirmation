package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.domain.Chapter;
import com.paulograbin.confirmation.exception.NotFoundException;
import com.paulograbin.confirmation.persistence.ChapterRepository;
import com.paulograbin.confirmation.web.ChapterCreationRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class ChapterService {

    @Resource
    private ChapterRepository chapterRepository;

    public Chapter fetchById(long chapterId) {
        return chapterRepository.findById(chapterId)
                .orElseThrow(() -> new NotFoundException("Chapter " + chapterId + " not found!"));
    }

    public Chapter createChapter(ChapterCreationRequest creationRequest) {
        // validate if chapter already exist

        Chapter chapterToCreate = new Chapter();
        chapterToCreate.setId(creationRequest.getId());
        chapterToCreate.setName(creationRequest.getName());

        return chapterRepository.save(chapterToCreate);
    }

    public long count() {
        return chapterRepository.count();
    }
}
