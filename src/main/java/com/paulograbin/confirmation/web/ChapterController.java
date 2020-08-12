package com.paulograbin.confirmation.web;

import com.paulograbin.confirmation.domain.Chapter;
import com.paulograbin.confirmation.persistence.ChapterRepository;
import com.paulograbin.confirmation.service.ChapterService;
import com.paulograbin.confirmation.usecases.ChapterCreationRequest;
import com.paulograbin.confirmation.web.dto.ChapterDTO;
import com.paulograbin.confirmation.web.dto.UserDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;


@RestController
@RequestMapping(value = "/chapter")
public class ChapterController {

    private static final Logger log = LoggerFactory.getLogger(ChapterController.class);

    @Resource
    private ChapterService chapterService;

    @Resource
    private ModelMapper modelMapper;

    @GetMapping
    @Cacheable("chapters")
    public List<ChapterDTO> fetchAllChapters() {
        log.info("Listing all chapters");

        Iterable<Chapter> eventIterator = chapterService.findAll();
        List<Chapter> arrayList = Lists.from(eventIterator.iterator());

        return arrayList.stream()
                .map(u -> modelMapper.map(u, ChapterDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ChapterDTO listChapter(@PathVariable long id) {
        log.info(format("Fetching chapter %d", id));

        Chapter chapter = chapterService.fetchById(id);
        ChapterDTO chapterDTO = modelMapper.map(chapter, ChapterDTO.class);

        chapterDTO.setMembers(chapter.getUsers().stream()
                .map(u -> modelMapper.map(u, UserDTO.class))
                .collect(Collectors.toList()));

        return chapterDTO;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ChapterDTO createNewChapter(@Valid @RequestBody ChapterCreationRequest creationRequest) {
        log.info("Creating new chapter {}", creationRequest);

        Chapter createdChapter = chapterService.createChapter(creationRequest);

        return modelMapper.map(createdChapter, ChapterDTO.class);
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ChapterDTO updateChapter(@Valid @RequestBody ChapterCreationRequest updateRequest) {
        log.info("Updating chapter {}", updateRequest);

        Chapter createdChapter = chapterService.update(updateRequest, "a");

        return modelMapper.map(createdChapter, ChapterDTO.class);
    }

}
