package eu.seatter.homemeasurement.collector.services.alert.email;

import eu.seatter.homemeasurement.collector.model.AlertContactGroup;
import eu.seatter.homemeasurement.collector.model.SensorRecord;
import eu.seatter.homemeasurement.collector.services.alert.AlertContactJSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 12/04/2019
 * Time: 13:14
 */
@Slf4j
@Service
public class EmailAlertServiceImpl implements EmailAlertService {

    private final JavaMailSender mailSender;
    private TemplateEngine templateEngine;

    private SensorRecord sensorRecord;

    public EmailAlertServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendAlert(SensorRecord sensorRecord) throws MessagingException {
        this.sensorRecord = sensorRecord;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "utf-8");

        try {
            String[] recipients = getAddress();
            helper.setTo(recipients);
        } catch (MessagingException ex) {
            throw new MessagingException("Error adding mail recipients to mailer message");
        }
        try {
            helper.setSubject(getSubject());
        } catch (MessagingException ex) {
            throw new MessagingException("No email recipients defined");
        }
        try {
            message.setContent(getContent(), "text/html");
        } catch (MessagingException ex) {
            throw new MessagingException("Error occurred setting the email content");
        }

        try {
            mailSender.send(message);
        } catch (MailException ex) {
            throw new MessagingException("An error occurred sending the email : " + ex.getMessage());
        }

    }


    public String[] getAddress() throws IllegalArgumentException {
        if(sensorRecord.getAlertgroup().isEmpty()) {
            return null;
        }
        String alertGroup = sensorRecord.getAlertgroup();
        AlertContactGroup ag = AlertContactJSON.GetContactsForGroup(alertGroup).orElse(new AlertContactGroup());

        if(ag.getAddress().isEmpty()) {
            throw new java.lang.IllegalArgumentException("No recipients found for alertgroup:" + alertGroup);
        }
        log.debug("Email recipients : " + ag.getAddress());
        return (ag.getAddress().split(";"));
    }

    public String getSubject() {
        String subject = "Home Monitor Alert - " + sensorRecord.getTitle() + " - " + sensorRecord.getValue() + sensorRecord.getMeasurementUnit().toString();
        log.debug("Email Subject : " + subject);
        return subject;
    }

    public String getContent() {
        Context context = new Context();
        context.setVariable("temperature", sensorRecord.getValue().toString() + sensorRecord.getMeasurementUnit().toString());
        context.setVariable("date", sensorRecord.getMeasureTimeUTC());
        return templateEngine.process("AlertEmailTemplate", context);
    }
}
