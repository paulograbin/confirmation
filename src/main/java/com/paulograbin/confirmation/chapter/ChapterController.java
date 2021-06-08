package com.paulograbin.confirmation.chapter;

import com.paulograbin.confirmation.chapter.usecases.readchapter.ReadChapterRequest;
import com.paulograbin.confirmation.chapter.usecases.readchapter.ReadChapterResponse;
import com.paulograbin.confirmation.chapter.usecases.readchapter.ReadChapterUseCase;
import com.paulograbin.confirmation.user.User;
import com.paulograbin.confirmation.user.UserRepository;
import com.paulograbin.confirmation.security.jwt.CurrentUser;
import com.paulograbin.confirmation.usecases.ChapterCreationRequest;
import com.paulograbin.confirmation.web.dto.ChapterDTO;
import com.paulograbin.confirmation.web.dto.UserDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;


@CrossOrigin("*")
@RestController
@RequestMapping(value = "/chapter")
public class ChapterController {

    private static final Logger log = LoggerFactory.getLogger(ChapterController.class);

    @Resource
    private ChapterService chapterService;

    @Resource
    private ChapterRepository chapterRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private ModelMapper modelMapper;

    @GetMapping
//    @Cacheable("chapters")
    public ResponseEntity<List<ChapterDTO>> fetchAllChapters() {
        log.info("Listing all chapters");

        Iterable<Chapter> eventIterator = chapterService.findAll();
        List<Chapter> chapterList = Lists.from(eventIterator.iterator());

        CacheControl cc = CacheControl.maxAge(Duration.ofMinutes(10)).cachePrivate();


        List<ChapterDTO> dtoList = chapterList.stream()
                .map(u -> modelMapper.map(u, ChapterDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .cacheControl(cc)
                .body(dtoList);
    }

    @GetMapping(path = "/meucapitulo", produces = MediaType.APPLICATION_JSON_VALUE)
//    @Cacheable(value = "chapterMembers", key = "#currentUser.chapter.id")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ReadChapterResponse> mychapter2(@CurrentUser User currentUser) {
        log.info("Fetching chapter for user {}", currentUser.getId());

        ReadChapterRequest request = new ReadChapterRequest();
        request.chapterId = currentUser.getChapter().getId();
        request.requestingUser = currentUser.getId();

        ReadChapterResponse response = new ReadChapterUseCase(request, chapterRepository, userRepository).execute();

        CacheControl cc = CacheControl.maxAge(Duration.ofDays(1)).cachePrivate();
        return ResponseEntity
                .ok()
//                .cacheControl(cc)
                .body(response);
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
    public ChapterDTO createNewChapter( @RequestBody ChapterCreationRequest creationRequest) {
        log.info("Creating new chapter {}", creationRequest);

        Chapter createdChapter = chapterService.createChapter(creationRequest);

        return modelMapper.map(createdChapter, ChapterDTO.class);
    }

    @PutMapping(path = "/{chapterId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ChapterDTO updateChapter(@PathVariable("chapterId") long chapterId, @RequestBody ChapterCreationRequest updateRequest) {
        log.info("Updating chapter {}", updateRequest);

        Chapter createdChapter = chapterService.update(chapterId, updateRequest);

        return modelMapper.map(createdChapter, ChapterDTO.class);
    }

    @DeleteMapping(path = "/{chapterId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteCapter(@PathVariable("chapterId") long chapterId) {
        log.info("Deleting chapter {}", chapterId);

        chapterService.deleteChapter(chapterId);
    }
}
