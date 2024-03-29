package com.paulograbin.confirmation.chapter;

import com.paulograbin.confirmation.exception.InvalidRequestException;
import com.paulograbin.confirmation.exception.NotFoundException;
import com.paulograbin.confirmation.usecases.ChapterCreationRequest;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


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

    public Chapter update(Long id, ChapterCreationRequest updateRequest) {
        if (!id.equals(updateRequest.getId())) {
            throw new InvalidRequestException("Provided id and request don't match");
        }

        Chapter chapterToUpdate = fetchById(updateRequest.getId());
        chapterToUpdate.setId(updateRequest.getId());
        chapterToUpdate.setName(updateRequest.getName());

        return chapterRepository.save(chapterToUpdate);
    }

    public void deleteChapter(long chapterId) {
        Chapter chapter = fetchById(chapterId);

        chapterRepository.delete(chapter);
    }

    public void update(Chapter chapter) {
        chapterRepository.save(chapter);
    }

    public Iterable<Chapter> findAll() {
        return chapterRepository.findAll();
    }
}
