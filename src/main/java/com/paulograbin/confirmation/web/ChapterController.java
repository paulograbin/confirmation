package com.paulograbin.confirmation.web;

import com.paulograbin.confirmation.domain.Chapter;
import com.paulograbin.confirmation.persistence.ChapterRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping(value = "/chapter")
public class ChapterController {

    @Resource
    private ChapterRepository chapterRepository;

    @GetMapping
    public Iterable<Chapter> fetchAllChapters() {
        return chapterRepository.findAll();
    }

}
