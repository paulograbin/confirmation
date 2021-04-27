package com.paulograbin.confirmation.chapter.usecases.readchapter;

import com.paulograbin.confirmation.chapter.Chapter;
import com.paulograbin.confirmation.chapter.ChapterRepository;
import com.paulograbin.confirmation.user.User;
import com.paulograbin.confirmation.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;


public class ReadChapterUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ReadChapterUseCase.class);

    ReadChapterRequest request;
    ReadChapterResponse response;
    ChapterRepository repository;
    UserRepository userRepository;

    public ReadChapterUseCase(ReadChapterRequest request, ChapterRepository repository, UserRepository userRepository) {
        this.response = new ReadChapterResponse();

        this.request = request;
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public ReadChapterResponse execute() {
        logger.info("Executing use case with request {}", request);

        if (isValid()) {
            fetchRequest();
        } else {
            setErrors();
        }

        logger.info("Returning response {}", response);
        return response;
    }


    private boolean isValid() {
        Optional<User> byId = userRepository.findById(request.requestingUser);
        if (byId.isEmpty()) {
            return false;
        }

        User user = byId.get();
        if (user.getChapter().getId() != request.getChapterId() && !user.isAdmin()) {
            return false;
        }

        return true;
    }

    private void setErrors() {
        // todo complete errors
    }

    private void fetchRequest() {
        Optional<Chapter> byId = repository.findById(request.chapterId);
        Chapter chapter = byId.get();

        response.id = chapter.getId();
        response.name = chapter.getName();

        Set<User> users = chapter.getUsers();
        response.memberCount = users.size();

        for (User user : users) {
            ReadChapterResponse.Member e = new ReadChapterResponse.Member(user.getFirstName(),
                    user.getLastName(),
                    user.getUsername(),
                    user.isActive());

            response.members.add(e);
        }

        response.members.sort(Comparator.comparing(ReadChapterResponse.Member::getFirstName));
    }
}
