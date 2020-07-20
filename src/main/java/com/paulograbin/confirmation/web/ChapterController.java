package com.paulograbin.confirmation.web;

import com.paulograbin.confirmation.domain.Chapter;
import com.paulograbin.confirmation.persistence.ChapterRepository;
import com.paulograbin.confirmation.service.ChapterService;
import com.paulograbin.confirmation.usecases.ChapterCreationRequest;
import com.paulograbin.confirmation.web.dto.ChapterDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(value = "/chapter")
public class ChapterController {

    private static final Logger log = LoggerFactory.getLogger(ChapterController.class);

    @Resource
    private ChapterService chapterService;

    @Resource
    private ChapterRepository chapterRepository;

    @Resource
    private ModelMapper modelMapper;

    @GetMapping
    public List<ChapterDTO> fetchAllChapters() {
        log.info("Listing all chapters");

        Iterable<Chapter> eventIterator = chapterRepository.findAll();
        List<Chapter> arrayList = Lists.from(eventIterator.iterator());

        return arrayList.stream()
                .map(u -> modelMapper.map(u, ChapterDTO.class))
                .collect(Collectors.toList());
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ChapterDTO createNewChapter(@Valid @RequestBody ChapterCreationRequest creationRequest) {
        log.info("Creating new chapter {}", creationRequest);

        Chapter createdChapter = chapterService.createChapter(creationRequest);

        return modelMapper.map(createdChapter, ChapterDTO.class);
    }

}
