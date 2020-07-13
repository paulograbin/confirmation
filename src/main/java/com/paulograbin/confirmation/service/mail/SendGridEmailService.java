package com.paulograbin.confirmation.service.mail;

import com.paulograbin.confirmation.domain.User;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.sendgrid.SendGridProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;


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

            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            System.out.println("Deu pau");
        }
    }

    @Override
    public void sendPasswordChangedMail(User userFromDatabase) {
        Email from = new Email("plgrabin@gmail.com");
        Email to = new Email(userFromDatabase.getEmail());
        String subject = "Senha modificada";

        var emailText = String.format("Olá %s, conforme sua solicitação sua senha foi alterada com sucesso.", userFromDatabase.getFirstName());

        Content content = new Content("text/plain", "Olá");
        Mail mail = new Mail(from, subject, to, content);

        sendMail(mail);
    }
}
