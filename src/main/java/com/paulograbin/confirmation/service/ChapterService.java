package com.paulograbin.confirmation.service;

import com.paulograbin.confirmation.domain.Chapter;
import com.paulograbin.confirmation.exception.NotFoundException;
import com.paulograbin.confirmation.persistence.ChapterRepository;
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
}
