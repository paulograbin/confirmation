package com.paulograbin.confirmation.usecases.event.creation;

import com.paulograbin.confirmation.DateUtils;
import com.paulograbin.confirmation.domain.Event;
import com.paulograbin.confirmation.persistence.EventRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class EventCreationUseCase {

    private EventCreationRequest request;
    private EventCreationResponse response;
    private EventRepository eventRepository;

    public void execute(EventCreationRequest request, EventCreationResponse response, EventRepository eventRepository) {
        this.request = request;
        this.response = response;
        this.eventRepository = eventRepository;

        isValid();
        createEvent();
    }

    private void createEvent() {
        try {
            Event eventToCreate = new Event();
            eventToCreate.setTitle(request.getTitle());
            eventToCreate.setDescription(request.getDescription());
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

        } catch (DateTimeParseException e) {
            response.invalidDate = true;
        }
    }

    private void isValid() {
        if (isBlank(request.getTitle()) || request.getTitle().length() < 5) {
            response.invalidTitle = true;
            response.errorMessage = "Título do evento precisa ter pelo menos 5 letras";
        }

        if (isBlank(request.getDescription()) || request.getDescription().length() < 5) {
            response.invalidDescription = true;
            response.errorMessage = "Descrição do evento precisa ter pelo menos 5 letras";
        }

        if (request.getAddress().isEmpty()) {
            response.invalidAddress = true;
            response.errorMessage = "Faltou informar o endereço";
        }

        if (request.getTime() == null) {
            response.invalidTime = true;
            response.errorMessage = "Faltou informar o horário do evento";
        }

        if (request.getCreator().getChapter() == null) {
            response.invalidChapter = true;
            response.errorMessage = "Criador do evento precisa pertencer a algum capitulo";
        }
    }

}
