package com.paulograbin.confirmation.service.mail;

import com.paulograbin.confirmation.user.User;
import com.paulograbin.confirmation.userequest.UserRequest;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.sendgrid.SendGridProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@Profile("production")
public class SendGridEmailService implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(SendGridEmailService.class);

    public static final String FROM_EMAIL_ADDRESS = "paulograbin@gmail.com";
    public static final String CC_EMAIL_ADDRESS = "pl.grabin@gmail.com";

    @Value("${sendgrid.template.user.created}")
    public String REQUEST_CREATED_EMAIL_TEMPLATE;

    @Value("${sendgrid.template.event.created}")
    public String EVENT_CREATED_EMAIL_TEMPLATE;

    @Resource
    private SendGridProperties properties;

    private void sendMail(Mail mail) {
        SendGrid sg = new SendGrid(properties.getApiKey());
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            logger.info("Mail send to {}", mail.toString());
            logger.info("Personalization {}", mail.getPersonalization());
            logger.info("Status code: {}", response.getStatusCode());
            logger.info("Headers: {}", response.getHeaders());
        } catch (IOException ex) {
            System.out.println("Deu pau");
        }
    }

    @Override
    public void sendUserRequestCreatedMail(UserRequest userRequest) {
        String subject = "Usuário criado";
        Email from = new Email(FROM_EMAIL_ADDRESS);

        Email to = new Email(userRequest.getEmail());
        Email cc = new Email(CC_EMAIL_ADDRESS);

        final var personalization = new Personalization();
        personalization.addDynamicTemplateData("firstName", userRequest.getFirstName());
        personalization.addDynamicTemplateData("requestNumber", userRequest.getCode());
        personalization.addTo(to);
        personalization.addTo(cc);

        Mail mail = new Mail();
        mail.setTemplateId(REQUEST_CREATED_EMAIL_TEMPLATE);
        mail.setFrom(from);
        mail.setSubject(subject);
        mail.addPersonalization(personalization);

        sendMail(mail);
    }

    @Override
    public void sendEventCreatedMail(Map<String, String> emailsAndNames, String chapterName) {
        if (chapterName.isEmpty()) {
            logger.info("Chapter name or master name are invalid, won't generate");
            return;
        }

        List<Mail> mailsToSend = new ArrayList<>();

        String subject = "Nova cerimônia criada";
        Email from = new Email(FROM_EMAIL_ADDRESS);

        emailsAndNames.entrySet().forEach(e -> {
            Email to = new Email(e.getKey());
            Email cc = new Email(CC_EMAIL_ADDRESS);

            final var personalization = new Personalization();
            personalization.addDynamicTemplateData("nome", e.getValue());
            personalization.addDynamicTemplateData("nome_capitulo", chapterName);
            personalization.addTo(to);
            personalization.addTo(cc);

            Mail mail = new Mail();
            mail.setTemplateId(EVENT_CREATED_EMAIL_TEMPLATE);
            mail.setFrom(from);
            mail.setSubject(subject);
            mail.addPersonalization(personalization);

            mailsToSend.add(mail);
        });

        mailsToSend.stream().forEach(this::sendMail);
    }

    @Override
    public void sendPasswordChangedMail(User userFromDatabase) {
        String subject = "Senha modificada";
        Email from = new Email(FROM_EMAIL_ADDRESS);

        Email to = new Email(userFromDatabase.getEmail());
        Email cc = new Email(CC_EMAIL_ADDRESS);

        final var personalization = new Personalization();
        personalization.addTo(to);
        personalization.addTo(cc);

        var emailText = String.format("Olá %s, conforme sua solicitação sua senha do Confirmação DeMolay foi alterada com sucesso.", userFromDatabase.getFirstName());
        Content content = new Content("text/plain", emailText);

        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setSubject(subject);
        mail.addPersonalization(personalization);
        mail.addContent(content);

        sendMail(mail);
    }
}
