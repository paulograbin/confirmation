package com.paulograbin.confirmation.service.mail;

import com.paulograbin.confirmation.email.EmailMessageEntity;
import com.paulograbin.confirmation.email.EmailMessageRepository;
import com.paulograbin.confirmation.email.EmailParameterEntity;
import com.paulograbin.confirmation.email.EmailParameterRepository;
import com.paulograbin.confirmation.passwordreset.PasswordRequest;
import com.paulograbin.confirmation.user.User;
import com.paulograbin.confirmation.userequest.UserRequest;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
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
import java.util.List;
import java.util.Map;


@Service
@Profile("production")
public class SendGridEmailService implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(SendGridEmailService.class);

    public static final String FROM_EMAIL_ADDRESS = "paulograbin@gmail.com";
    public static final String CC_EMAIL_ADDRESS = "paulo.grabin@kps.com";

    @Value("${sendgrid.template.user.created}")
    public String REQUEST_CREATED_EMAIL_TEMPLATE;

    @Value("${sendgrid.template.event.created}")
    public String EVENT_CREATED_EMAIL_TEMPLATE;

    @Value("${sendgrid.template.password.forgot}")
    public String PASSWORD_FORGOT_EMAIL_TEMPLATE;

    @Resource
    private SendGridProperties properties;

    @Resource
    private EmailMessageRepository emailMessageRepository;

    @Resource
    private EmailParameterRepository emailParameterRepository;


    private void sendMail(Mail mail) {
        SendGrid sg = new SendGrid(properties.getApiKey());
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            logger.info("****");
            logger.info(mail.build());
            logger.info("****");
            Response response = sg.api(request);

            logger.info("Mail send to {}", mail.toString());
            logger.info("Personalization {}", mail.getPersonalization());
            logger.info("Status code: {}", response.getStatusCode());
            logger.info("Headers: {}", response.getHeaders());
        } catch (IOException ex) {
            logger.error("Deu pau no envio de email: {}", ex.getMessage());
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

//    @Override
//    public void sendUserRequestCreatedMail(UserRequest userRequest) {
//        var email = new EmailMessageEntity();
//        email.setSubject("Usuário criado");
//        email.setFromAddress(FROM_EMAIL_ADDRESS);
//        email.setToAddress(userRequest.getEmail());
//        email.setCcAddress(CC_EMAIL_ADDRESS);
//        email.setTemplateId(REQUEST_CREATED_EMAIL_TEMPLATE);
//        emailMessageRepository.save(email);
//
//        List<EmailParameterEntity> parameters = email.getParameters();
//
//        EmailParameterEntity p1 = new EmailParameterEntity();
//        p1.setEmailMessage(email);
//        p1.setParameterName("firstName");
//        p1.setParameterValue(userRequest.getFirstName());
//
//        EmailParameterEntity p2 = new EmailParameterEntity();
//        p2.setEmailMessage(email);
//        p2.setParameterName("requestNumber");
//        p2.setParameterValue(userRequest.getCode().toString());
//
//        emailParameterRepository.saveAll(List.of(p1, p2));
//
//        email.setParameters(parameters);
//        emailMessageRepository.save(email);
//    }

    @Override
    public void sendEventCreatedMail(Map<String, String> emailsAndNames, String chapterName) {
        if (chapterName.isEmpty()) {
            logger.info("Chapter name or master name are invalid, won't generate");
            return;
        }

        var subject = "Nova cerimônia criada";

        emailsAndNames.entrySet().forEach(e -> {
            var toAddress = e.getKey();

            EmailMessageEntity email = new EmailMessageEntity();
            email.setTemplateId(EVENT_CREATED_EMAIL_TEMPLATE);
            email.setFromAddress(FROM_EMAIL_ADDRESS);
            email.setToAddress(toAddress);
            email.setCcAddress(CC_EMAIL_ADDRESS);
            email.setSubject(subject);
            email.setChapterName(chapterName);

            emailMessageRepository.save(email);

            List<EmailParameterEntity> parameters = email.getParameters();

            EmailParameterEntity p1 = new EmailParameterEntity();
            p1.setEmailMessage(email);
            p1.setParameterName("nome");
            p1.setParameterValue(e.getValue());

            EmailParameterEntity p2 = new EmailParameterEntity();
            p2.setEmailMessage(email);
            p2.setParameterName("nome_capitulo");
            p2.setParameterValue(chapterName);

            emailParameterRepository.saveAll(List.of(p1, p2));

            email.setParameters(parameters);
            emailMessageRepository.save(email);
        });
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

    @Override
    public void sendForgotPasswordMail(PasswordRequest passwordRequest, User user) {
        sendMailWithDynamicTemplate(passwordRequest, user);

        String subject = "Recuperação de senha";
        Email from = new Email(FROM_EMAIL_ADDRESS);

        Email to = new Email(user.getEmail());
        Email cc = new Email(CC_EMAIL_ADDRESS);

        final var personalization = new Personalization();
        personalization.addDynamicTemplateData("firstName", user.getFirstName());
        personalization.addDynamicTemplateData("passwordRequest", passwordRequest.getCode());
        personalization.addTo(to);
        personalization.addTo(cc);

        Mail mail = new Mail();
        mail.setTemplateId(PASSWORD_FORGOT_EMAIL_TEMPLATE);
        mail.setFrom(from);
        mail.setSubject(subject);
        mail.addPersonalization(personalization);

        sendMail(mail);
    }

    private void sendMailWithDynamicTemplate(PasswordRequest passwordRequest, User user) {

    }
}
