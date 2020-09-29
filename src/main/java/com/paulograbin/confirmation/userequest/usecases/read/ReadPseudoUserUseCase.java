package com.paulograbin.confirmation.userequest.usecases.read;

import com.paulograbin.confirmation.userequest.UserRequest;
import com.paulograbin.confirmation.userequest.UserRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

import static com.paulograbin.confirmation.DateUtils.getCurrentDate;
import static com.paulograbin.confirmation.DateUtils.parseDateToString;

public class ReadPseudoUserUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ReadPseudoUserUseCase.class);

    private final ReadPseudoUserResponse response;
    private final ReadPseudoUserRequest request;
    private final UserRequestRepository repository;
    private UUID requestId;

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
        try {
            this.requestId = UUID.fromString(request.requestId);
        } catch (IllegalArgumentException e) {
            return false;
        }

        Optional<UserRequest> byId = repository.findById(requestId);
        if (byId.isEmpty()) {
            return false;
        }

        UserRequest request = byId.get();
        if (getCurrentDate().isAfter(request.getExpirationDate())) {
            return false;
        }

        if (request.getConvertionDate() != null) {
            return false;
        }

        return true;
    }

    private void setErrors() {
        try {
            this.requestId = UUID.fromString(request.requestId);

            Optional<UserRequest> byId = repository.findById(UUID.fromString(request.requestId));

            if (byId.isEmpty()) {
                response.errorMessage = "Requisição não encontrada";
                response.requestNotFound = true;
                return;
            }

            UserRequest request = byId.get();

            if (getCurrentDate().isAfter(request.getExpirationDate())) {
                response.errorMessage = "Essa requisição já não é mais válida";
                response.expired = true;
            }

            if (request.getConvertionDate() != null) {
                response.errorMessage = "Essa requisição já não é mais válida";
                response.expired = true;
            }
        } catch (IllegalArgumentException e) {
            response.errorMessage = "Requisição não encontrada";
            response.requestNotFound = true;
        }
    }

    private void fetchRequest() {
        Optional<UserRequest> byId = repository.findById(UUID.fromString(request.requestId));

        UserRequest requestFromDatabase = byId.get();

        response.successful = true;

        response.requestId = requestFromDatabase.getId().toString();
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
