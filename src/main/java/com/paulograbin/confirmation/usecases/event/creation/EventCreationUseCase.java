package com.paulograbin.confirmation.usecases.event.creation;

import com.paulograbin.confirmation.DateUtils;
import com.paulograbin.confirmation.domain.Event;
import com.paulograbin.confirmation.persistence.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.apache.commons.lang3.StringUtils.isBlank;


public class EventCreationUseCase {

    private static final Logger logger = LoggerFactory.getLogger(EventCreationUseCase.class);

    public static final int DESCRIPTION_MAX_LENGTH = 250;
    public static final int DESCRIPTION_MINIMUM_LENGTH = 5;
    public static final int TITLE_MINIMUM_LENGTH = 5;

    private final EventCreationRequest request;
    private final EventCreationResponse response;
    private final EventRepository eventRepository;

    public EventCreationUseCase(EventCreationRequest request, EventCreationResponse eventCreationResponse, EventRepository eventRepository) {
        this.request = request;
        this.response = eventCreationResponse;
        this.eventRepository = eventRepository;
    }

    public void execute() {
        logger.info("Executing event creation with the following request");
        logger.info(request.toString());

        if (isValid()) {
            createEvent();
        } else {
            returnErrors();
        }
    }

    private void returnErrors() {
        if (isBlank(request.getTitle()) || request.getTitle().length() < TITLE_MINIMUM_LENGTH) {
            response.invalidTitle = true;
            response.errorMessage = "Título do evento precisa ter pelo menos 5 letras";
        }

        if (isBlank(request.getDescription()) || request.getDescription().length() < DESCRIPTION_MINIMUM_LENGTH) {
            response.invalidDescription = true;
            response.errorMessage = "Descrição do evento precisa ter pelo menos 5 letras";
        }

        if (isBlank(request.getDescription()) || request.getDescription().length() >= DESCRIPTION_MAX_LENGTH) {
            response.invalidDescription = true;
            response.errorMessage = "Descrição deve conter menos de 500 caracteres";
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
            DateUtils.getDateFromString(request.getDate());
        } catch (DateTimeParseException e) {
            response.invalidDate = true;
            response.errorMessage = "Data inválida";
        }

        if (DateUtils.getDateFromString(request.getDate()).isBefore(LocalDate.now())) {
            response.invalidDate = true;
            response.errorMessage = "Cerimônias não podem ser criadas com data no passado";
        }

        if (request.getTime() == null) {
            response.invalidTime = true;
            response.errorMessage = "Faltou informar o horário do evento";
        }

        if (request.getCreator().getChapter() == null) {
            response.invalidChapter = true;
            response.errorMessage = "Criador do evento precisa pertencer a algum capitulo";
        }

        logger.info("Event creation failed");
        logger.info(response.toString());
    }

    private void createEvent() {
        try {
            Event eventToCreate = new Event();
            eventToCreate.setTitle(request.getTitle());

            if (request.getDescription().length() >= DESCRIPTION_MAX_LENGTH) {
                eventToCreate.setDescription(request.getDescription().substring(0, DESCRIPTION_MAX_LENGTH));
            } else {
                eventToCreate.setDescription(request.getDescription());
            }

            eventToCreate.setAddress(request.getAddress());
            eventToCreate.setChapter(request.getCreator().getChapter());
            eventToCreate.setPublished(request.isPublished());
            eventToCreate.setTime(request.getTime());
            eventToCreate.setCreationDate(DateUtils.getCurrentDate());

            LocalDate eventDate = DateUtils.getDateFromString(request.getDate());
            eventToCreate.setDate(eventDate);
            eventToCreate.setCreator(request.getCreator());

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

        if (isBlank(request.getDescription()) || request.getDescription().length() < DESCRIPTION_MINIMUM_LENGTH || request.getDescription().length() > DESCRIPTION_MAX_LENGTH) {
            return false;
        }

        if (request.getAddress().isEmpty()) {
            return false;
        }

        if (request.getDate() == null) {
            return false;
        }

        if (DateUtils.getDateFromString(request.getDate()).isBefore(LocalDate.now())) {
            return false;
        }

        if (request.getTime() == null) {
            return false;
        }

        if (request.getCreator().getChapter() == null) {
            return false;
        }

        return true;
    }
}
