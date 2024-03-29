package com.paulograbin.confirmation.event.usecases.creation;

import com.paulograbin.confirmation.DateUtils;
import com.paulograbin.confirmation.chapter.ChapterRepository;
import com.paulograbin.confirmation.user.User;
import com.paulograbin.confirmation.event.Event;
import com.paulograbin.confirmation.event.repository.EventRepository;
import com.paulograbin.confirmation.participation.Participation;
import com.paulograbin.confirmation.participation.ParticipationRepository;
import com.paulograbin.confirmation.participation.ParticipationStatus;
import com.paulograbin.confirmation.user.UserRepository;
import com.paulograbin.confirmation.service.mail.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final ChapterRepository chapterRepository;

    private final EmailService emailService;

    private String chapterName = "";
    private User eventCreator;

    public EventCreationUseCase(EventCreationRequest request, EventRepository eventRepository, ParticipationRepository participationRepository, UserRepository userRepository, EmailService emailService, ChapterRepository chapterRepository) {
        this.request = request;
        this.eventRepository = eventRepository;
        this.participationRepository = participationRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.chapterRepository = chapterRepository;

        this.response = new EventCreationResponse();
    }

    public EventCreationResponse execute() {
        logger.info("Executing event creation with the following request");
        logger.info(request.toString());

        if (isValid()) {
            createEvent();
            confirmMasterPresence();
            sendMailToInvitedUsers();
        } else {
            returnErrors();
        }

        logger.info("Event created successfully");
        logger.info(response.toString());
        return response;
    }

    private void sendMailToInvitedUsers() {
        logger.info("Sending event created mail to all active members");
        logger.info("Chapter name: {}", chapterName);

        List<User> allActiveMembers = userRepository.findAllByChapterIdAndActiveTrue(eventCreator.getChapter().getId());
        Map<String, String> invitedUsers = allActiveMembers.stream().collect(Collectors.toMap(User::getEmail, User::getFirstName));
        logger.info("Sending event created mail to: {}", invitedUsers);

        emailService.sendEventCreatedMail(invitedUsers, chapterName);
    }

    private void confirmMasterPresence() {
        Event event = eventRepository.findById(response.createdEventId).get();
        User user = userRepository.findById(request.getCreatorId()).get();

        Participation masterParticipation = new Participation();
        masterParticipation.setUser(user);
        masterParticipation.setEvent(event);
        masterParticipation.setConfirmationDate(DateUtils.getCurrentDate());
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

        LocalDate parsedDate;
        try {
            parsedDate = DateUtils.getDateFromString(request.getDate());

            if (parsedDate.isBefore(DateUtils.getCurrentDate().toLocalDate())) {
                response.invalidDate = true;
                response.errorMessage = "Cerimônias não podem ser criadas com data no passado";
            }
        } catch (DateTimeParseException e) {
            response.invalidDate = true;
            response.errorMessage = "Data inválida";

            return;
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

        User eventCreator = byId.get();

        if (eventCreator.getChapter() == null) {
            response.invalidChapter = true;
            response.errorMessage = "Criador do evento precisa pertencer a algum capitulo";
            return;
        }

        List<Event> allByChapterIdAndDate = eventRepository.findAllByChapterIdAndDate(eventCreator.getChapter().getId(), parsedDate);
        boolean hasEventWithSameDateNameAndTime = allByChapterIdAndDate
                .stream()
                .anyMatch(e -> e.getTitle().equalsIgnoreCase(request.getTitle()) && e.getTime().equals(request.getTime()));

        if (hasEventWithSameDateNameAndTime) {
            response.duplicated = true;
            response.errorMessage = "Já existe outro evento com esse mesmo nome, data e horário.";
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

            eventCreator = userRepository.findById(request.getCreatorId()).get();
            chapterName = eventCreator.getChapter().getName();

            eventToCreate.setAddress(request.getAddress());
            eventToCreate.setChapter(eventCreator.getChapter());
            eventToCreate.setPublished(true);
            eventToCreate.setTime(request.getTime());
            eventToCreate.setCreationDate(DateUtils.getCurrentDate());

            LocalDate eventDate = DateUtils.getDateFromString(request.getDate());
            eventToCreate.setDate(eventDate);
            eventToCreate.setCreator(eventCreator);

            Event createdEvent = eventRepository.save(eventToCreate);

            response.successful = true;
            response.createdEventId = createdEvent.getId();
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

        LocalDate requestParsedDate;
        try {
            requestParsedDate = DateUtils.getDateFromString(request.getDate());

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

        User eventCreator = byId.get();
        if (eventCreator.getChapter() == null) {
            return false;
        }

        List<Event> allByChapterIdAndDate = eventRepository.findAllByChapterIdAndDate(eventCreator.getChapter().getId(), requestParsedDate);
        boolean hasEventWithSameDateNameAndTime = allByChapterIdAndDate
                .stream()
                .anyMatch(e -> e.getTitle().equalsIgnoreCase(request.getTitle()) && e.getTime().equals(request.getTime()));
        if (hasEventWithSameDateNameAndTime) {
            return false;
        }

        return true;
    }
}
