package com.paulograbin.confirmation.usecases.user.harddelete;

import com.paulograbin.confirmation.participation.Participation;
import com.paulograbin.confirmation.user.User;
import com.paulograbin.confirmation.participation.ParticipationRepository;
import com.paulograbin.confirmation.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserHardDeleteUseCase {

    private static final Logger logger = LoggerFactory.getLogger(UserHardDeleteUseCase.class);

    private final UserHardDeleteRequest request;
    private final UserHardDeleteResponse response;
    private final UserRepository repository;
    private final ParticipationRepository participationRepository;

    public UserHardDeleteUseCase(UserHardDeleteRequest request, UserHardDeleteResponse response, UserRepository repository, ParticipationRepository participationRepository) {
        this.request = request;
        this.response = response;
        this.repository = repository;
        this.participationRepository = participationRepository;
    }

    public void execute() {
        logger.info("Executing use case...");

        if (isValid()) {
            removeEveryParticipation();
            removeUser();
        } else {
            returnErrors();
        }
    }

    private void removeEveryParticipation() {
        logger.info("Removing every participation from user {}", request.getUserToDeleteId());

        List<Participation> everyParticipationFromUser = participationRepository.findByUserId(request.getUserToDeleteId());
        logger.info("Found {} participations to delete", everyParticipationFromUser.size());

        participationRepository.deleteAll(everyParticipationFromUser);
    }

    private void returnErrors() {
        logger.info("Something went wrong while validating request...");

        if (!repository.existsById(request.getUserToDeleteId())) {
            response.errorMessage = "Usuário não encontrado com o id " + request.getUserToDeleteId();
            response.userNotFound = true;
        }

        User user = repository.findById(request.getRequestingUser()).orElseGet(User::new);
        if (!user.isAdmin()) {
            response.errorMessage = "This action can only be performed by an admin";
            response.notAdmin = true;
        }
    }

    private boolean isValid() {
        logger.info("Checking if request is valid");

        User user = repository.findById(request.getRequestingUser()).orElseGet(User::new);

        if (!user.isAdmin()) {
            return false;
        }

        return repository.existsById(request.getUserToDeleteId());
    }

    private void removeUser() {
        logger.info("Removing user {} from database", request.getUserToDeleteId());

        repository.deleteById(request.getUserToDeleteId());

        response.successful = true;
    }
}
