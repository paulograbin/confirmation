package com.paulograbin.confirmation.usecases.pseudouser.read;

import com.paulograbin.confirmation.domain.UserRequest;
import com.paulograbin.confirmation.persistence.UserRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static com.paulograbin.confirmation.DateUtils.parseDateToString;

public class ReadPseudoUserUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ReadPseudoUserUseCase.class);

    private final ReadPseudoUserResponse response;
    private final ReadPseudoUserRequest request;
    private final UserRequestRepository repository;

    public ReadPseudoUserUseCase(ReadPseudoUserRequest readPseudoUserRequest, UserRequestRepository repository) {
        this.response = new ReadPseudoUserResponse();

        this.request = readPseudoUserRequest;
        this.repository = repository;
    }

    public ReadPseudoUserResponse execute() {
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
        Optional<UserRequest> byId = repository.findById(request.requestId);

        if (byId.isEmpty()) {
            return false;
        }

        return true;
    }

    private void setErrors() {
        Optional<UserRequest> byId = repository.findById(request.requestId);

        if (byId.isEmpty()) {
            response.requestNotFound = true;
        }
    }

    private void fetchRequest() {
        Optional<UserRequest> byId = repository.findById(request.requestId);

        UserRequest requestFromDatabase = byId.get();

        response.successful = true;

        response.requestId = requestFromDatabase.getId();
        response.firstName = requestFromDatabase.getFirstName();
        response.lastName = requestFromDatabase.getLastName();
        response.email = requestFromDatabase.getEmail();
        response.creator = requestFromDatabase.getCreatedBy().getUsername();
        response.creatorId = requestFromDatabase.getCreatedBy().getId();

        if (requestFromDatabase.getUser() != null) {
            response.userId = requestFromDatabase.getUser().getId();
            response.username = requestFromDatabase.getUser().getUsername();
        } else {
            response.userId = null;
            response.username = "";
        }

        response.chapterId = requestFromDatabase.getChapter().getId();
        response.chapterName = requestFromDatabase.getChapter().getName();
        response.creationDate = parseDateToString(requestFromDatabase.getCreationDate());
        response.expirationDate = parseDateToString(requestFromDatabase.getExpirationDate());

        if (requestFromDatabase.getConvertionDate() != null) {
            response.conversionDate = parseDateToString(requestFromDatabase.getConvertionDate());
        } else {
            response.conversionDate = "";
        }
    }
}
