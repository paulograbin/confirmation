package com.paulograbin.confirmation.metrics.usecases.read;

import com.paulograbin.confirmation.DateUtils;
import com.paulograbin.confirmation.chapter.ChapterRepository;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.persistence.EventRepository;
import com.paulograbin.confirmation.persistence.UserRepository;
import com.paulograbin.confirmation.userequest.UserRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ReadMetricsUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ReadMetricsUseCase.class);

    private final ReadMetricsRequest request;
    private final ReadMetricsResponse response;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ChapterRepository chapterRepository;
    private final UserRequestRepository userRequestRepository;

    public ReadMetricsUseCase(ReadMetricsRequest readMetricsRequest, UserRepository userRepository, EventRepository eventRepository, ChapterRepository chapterRepository, UserRequestRepository userRequestRepository) {
        this.request = readMetricsRequest;

        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.chapterRepository = chapterRepository;
        this.userRequestRepository = userRequestRepository;

        this.response = new ReadMetricsResponse();
    }

    public ReadMetricsResponse execute() {
        logger.info("Executing usecase with request {}", request);

        if (isValid()) {
            gatherMetrics();
        } else {
            setErrors();
        }

        logger.info("Read metrics response {}", response);
        return response;
    }

    private void setErrors() {
        Optional<User> byId = userRepository.findById(request.requestingUser);
        if (byId.isEmpty()) {
            response.invalidUser = true;
            return;
        }

        User user = byId.get();
        if (!user.isAdmin()) {
            response.notAllowed = true;
        }
    }

    private void gatherMetrics() {
        gatherUserMetrics();
        gatherEventMetrics();
        gatherChapterMetrics();
        gatherUserRequestMetrics();

        response.successful = true;
    }

    private void gatherUserRequestMetrics() {
        response.totalUserRequests = userRequestRepository.count();
        response.convertedUserRequest = userRequestRepository.countAllByUserNotNull();
    }

    private void gatherChapterMetrics() {
        response.totalChapters = chapterRepository.count();
    }

    private void gatherEventMetrics() {
        response.totalEvents = eventRepository.count();
        response.publishedEvents = eventRepository.countAllByPublishedTrue();
        response.futureEvents = eventRepository.countAllByDateAfter(DateUtils.getCurrentDate().toLocalDate());
    }

    private void gatherUserMetrics() {
        response.totalUsers = userRepository.count();
        response.activeUsers = userRepository.countAllByActiveTrue();
        response.usersThatAlreadyLoggedIn = userRepository.countAllByLastLoginNotNull();
    }

    private boolean isValid() {
        Optional<User> byId = userRepository.findById(request.requestingUser);
        if (byId.isEmpty()) {
            return false;
        }

        User user = byId.get();
        if (!user.isAdmin()) {
            return false;
        }

        return true;
    }
}
