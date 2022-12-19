package com.paulograbin.confirmation.metrics;

import com.paulograbin.confirmation.chapter.ChapterRepository;
import com.paulograbin.confirmation.event.repository.EventRepository;
import com.paulograbin.confirmation.metrics.usecases.read.ReadMetricsRequest;
import com.paulograbin.confirmation.metrics.usecases.read.ReadMetricsResponse;
import com.paulograbin.confirmation.metrics.usecases.read.ReadMetricsUseCase;
import com.paulograbin.confirmation.participation.ParticipationRepository;
import com.paulograbin.confirmation.security.jwt.CurrentUser;
import com.paulograbin.confirmation.user.User;
import com.paulograbin.confirmation.user.UserRepository;
import com.paulograbin.confirmation.userequest.UserRequestRepository;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;


@RestController
@CrossOrigin("*")
@RequestMapping(value = "/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
public class MetricsController {

    private static final Logger logger = LoggerFactory.getLogger(MetricsController.class);

    @Resource
    UserRepository userRepository;

    @Resource
    ChapterRepository chapterRepository;

    @Resource
    EventRepository eventRepository;

    @Resource
    UserRequestRepository userRequestRepository;

    @Resource
    ParticipationRepository participationRepository;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ReadMetricsResponse> listAll(@CurrentUser User currentUser) {
        logger.info("Reading application metrics requested by user {}", currentUser.getId());

        ReadMetricsRequest readMetricsRequest = new ReadMetricsRequest();
        readMetricsRequest.requestingUser = currentUser.getId();

        ReadMetricsResponse response = new ReadMetricsUseCase(readMetricsRequest, userRepository, eventRepository, chapterRepository, userRequestRepository, participationRepository).execute();

        CacheControl cc = CacheControl.maxAge(Duration.ofMinutes(10)).cachePrivate();

        return ResponseEntity.ok()
                .cacheControl(cc)
                .body(response);
    }
}
