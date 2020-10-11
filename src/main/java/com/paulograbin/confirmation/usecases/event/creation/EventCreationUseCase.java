package com.paulograbin.confirmation.usecases.event.creation;

import com.paulograbin.confirmation.DateUtils;
import com.paulograbin.confirmation.domain.Event;
import com.paulograbin.confirmation.domain.Participation;
import com.paulograbin.confirmation.domain.ParticipationStatus;
import com.paulograbin.confirmation.domain.User;
import com.paulograbin.confirmation.persistence.EventRepository;
import com.paulograbin.confirmation.persistence.ParticipationRepository;
import com.paulograbin.confirmation.persistence.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;


public class EventCreationUseCase {

    private static final Logger logger = LoggerFactory.getLogger(EventCreationUseCase.class);

    public static final int DESCRIPTION_MAX_LENGTH = 500;
    public static final int DESCRIPTION_MINIMUM_LENGTH = 5;
    public static final int TITLE_MINIMUM_LENGTH = 5;
    public static final int TITLE_MAX_LENGTH = 250;

    private final EventCreationRequest request;
    private final EventCreationResponse response;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ParticipationRepository participationRepository;

    public EventCreationUseCase(EventCreationRequest request, EventRepository eventRepository, ParticipationRepository participationRepository, UserRepository userRepository) {
        this.request = request;
        this.eventRepository = eventRepository;
        this.participationRepository = participationRepository;
        this.userRepository = userRepository;

        this.response = new EventCreationResponse();
    }

    public EventCreationResponse execute() {
        logger.info("Executing event creation with the following request");
        logger.info(request.toString());

        if (isValid()) {
            createEvent();
            confirmMasterPresence();
        } else {
            returnErrors();
        }

        return response;
    }

    private void confirmMasterPresence() {
        Event event = eventRepository.findById(response.createdEventId).get();
        User user = userRepository.findById(request.getCreatorId()).get();

        Participation masterParticipation = new Participation();
        masterParticipation.setUser(user);
        masterParticipation.setEvent(event);
        masterParticipation.setStatus(ParticipationStatus.CONFIRMADO);

        participationRepository.save(masterParticipation);
    }

    private void returnErrors() {
        if (isBlank(request.getTitle()) || request.getTitle().length() < TITLE_MINIMUM_LENGTH) {
            response.invalidTitle = true;
            response.errorMessage = "Título do evento precisa ter pelo menos " + TITLE_MINIMUM_LENGTH + " letras";
        }

        if (isBlank(request.getDescription()) || request.getDescription().length() < DESCRIPTION_MINIMUM_LENGTH) {
            response.invalidDescription = true;
            response.errorMessage = "Descrição do evento precisa ter pelo menos " + DESCRIPTION_MINIMUM_LENGTH + " letras";
        }

        if (request.getAddress().isEmpty()) {
            response.invalidAddress = true;
            response.errorMessage = "Faltou informar o endereço";
        }

        if (request.getDate() == null) {
            response.invalidTime = true;
            response.errorMessage = "Faltou informar a data do evento";
        }

        try {
            LocalDate parsedDate = DateUtils.getDateFromString(request.getDate());

            if (parsedDate.isBefore(DateUtils.getCurrentDate().toLocalDate())) {
                response.invalidDate = true;
                response.errorMessage = "Cerimônias não podem ser criadas com data no passado";
            }
        } catch (DateTimeParseException e) {
            response.invalidDate = true;
            response.errorMessage = "Data inválida";
        }

        if (request.getTime() == null) {
            response.invalidTime = true;
            response.errorMessage = "Faltou informar o horário do evento";
        }

        Optional<User> byId = userRepository.findById(request.getCreatorId());
        if (byId.isEmpty()) {
            response.invalidCreator = true;
            response.errorMessage = "Criador do evento é um usuário inválido";
        }

        if (byId.get().getChapter() == null) {
            response.invalidChapter = true;
            response.errorMessage = "Criador do evento precisa pertencer a algum capitulo";
        }

        logger.info("Event creation failed");
        logger.info(response.toString());
    }

    private void createEvent() {
        try {
            Event eventToCreate = new Event();

            if (request.getTitle().length() > TITLE_MAX_LENGTH) {
                eventToCreate.setTitle(request.getTitle().substring(0, TITLE_MAX_LENGTH));
            } else {
                eventToCreate.setTitle(request.getTitle());
            }

            if (request.getDescription().length() >= DESCRIPTION_MAX_LENGTH) {
                eventToCreate.setDescription(request.getDescription().substring(0, DESCRIPTION_MAX_LENGTH));
            } else {
                eventToCreate.setDescription(request.getDescription());
            }

            User eventCreator = userRepository.findById(request.getCreatorId()).get();

            eventToCreate.setAddress(request.getAddress());
            eventToCreate.setChapter(eventCreator.getChapter());
            eventToCreate.setPublished(request.isPublished());
            eventToCreate.setTime(request.getTime());
            eventToCreate.setCreationDate(DateUtils.getCurrentDate());

            LocalDate eventDate = DateUtils.getDateFromString(request.getDate());
            eventToCreate.setDate(eventDate);
            eventToCreate.setCreator(eventCreator);

            Event createdEvent = eventRepository.save(eventToCreate);

            response.successful = true;
            response.createdEventId = createdEvent.getId();

            logger.info("Event created successfully");
            logger.info(response.toString());
        } catch (DateTimeParseException e) {
            response.invalidDate = true;
        }
    }

    private boolean isValid() {
        if (isBlank(request.getTitle()) || request.getTitle().length() < TITLE_MINIMUM_LENGTH) {
            return false;
        }

        if (isBlank(request.getDescription()) || request.getDescription().length() < DESCRIPTION_MINIMUM_LENGTH) {
            return false;
        }

        if (request.getAddress().isEmpty()) {
            return false;
        }

        if (request.getDate() == null) {
            return false;
        }

        try {
            LocalDate requestParsedDate = DateUtils.getDateFromString(request.getDate());

            if (requestParsedDate.isBefore(DateUtils.getCurrentDate().toLocalDate())) {
                return false;
            }
        } catch (DateTimeParseException e) {
            return false;
        }

        if (request.getTime() == null) {
            return false;
        }

        Optional<User> byId = userRepository.findById(request.getCreatorId());
        if (byId.isEmpty()) {
            return false;
        }

        User user = byId.get();
        if (user.getChapter() == null) {
            return false;
        }

        return true;
    }
}
