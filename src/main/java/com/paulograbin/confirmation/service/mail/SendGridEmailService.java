package com.paulograbin.confirmation.service.mail;

import com.paulograbin.confirmation.domain.User;
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
        Email from = new Email("plgrabin@gmail.com");

        Email to = new Email(userRequest.getEmail());
        Email cc = new Email("pl.grabin@gmail.com");

        final var personalization = new Personalization();
        personalization.addDynamicTemplateData("name", userRequest.getFirstName());
        personalization.addDynamicTemplateData("requestNumber", userRequest.getId());
        personalization.addTo(to);
        personalization.addTo(cc);

        Mail mail = new Mail();
        mail.setTemplateId("d-41eaeb82fef74c99b2d0c715c5f0bfb0");
        mail.setFrom(from);
        mail.setSubject(subject);
        mail.addPersonalization(personalization);

        sendMail(mail);
    }

    @Override
    public void sendEventCreatedMail(Map<String, String> emailsAndNames, String chapterName, String masterName) {
        if (chapterName.isEmpty() || masterName.isEmpty()) {
            logger.info("Chapter name or master name are invalid, won't generate");
            return;
        }

        List<Mail> mailsToSend = new ArrayList<>();

        String subject = "Nova cerimônia criada";
        Email from = new Email("plgrabin@gmail.com");

        emailsAndNames.entrySet().forEach(e -> {
            Email to = new Email(e.getKey());
            Email cc = new Email("pl.grabin@gmail.com");

            final var personalization = new Personalization();
            personalization.addDynamicTemplateData("nome", e.getValue());
            personalization.addDynamicTemplateData("nome_mc", masterName);
            personalization.addDynamicTemplateData("nome_capitulo", chapterName);
            personalization.addTo(to);
            personalization.addTo(cc);

            Mail mail = new Mail();
            mail.setTemplateId("d-c9297475c42b4c41b944228d64957bf4");
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
        Email from = new Email("plgrabin@gmail.com");

        Email to = new Email(userFromDatabase.getEmail());
        Email cc = new Email("pl.grabin@gmail.com");

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
