package eu.seatter.homemeasurement.collector.services.alert.email;

import eu.seatter.homemeasurement.collector.model.AlertContactGroup;
import eu.seatter.homemeasurement.collector.model.AlertDestination;
import eu.seatter.homemeasurement.collector.model.SensorRecord;
import eu.seatter.homemeasurement.collector.services.alert.AlertContactJSON;
import eu.seatter.homemeasurement.collector.services.alert.AlertType;
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
    private final TemplateEngine templateEngine;

    public EmailAlertServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendAlert(AlertType alertType, String alertMessage, SensorRecord sensorRecord) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "utf-8");
        try {
            try {
                String recipients = getRecipients(sensorRecord);
                if(recipients != null) {
                    helper.setTo(recipients.split(";"));
                }
            } catch (MessagingException ex) {
                throw new MessagingException("Error adding mail recipients to mailer message");
            }

            try {
                helper.setSubject(getSubject(sensorRecord));
            } catch (MessagingException ex) {
                throw new MessagingException("No email recipients defined");
            }

            try {
                if(alertType == AlertType.General) {
                    message.setContent(getContent("AlertGeneralMessageEmailTemplate",sensorRecord, alertMessage), "text/html");
                } else if(alertType == AlertType.Measurement) {
                    message.setContent(getContent(getAlertDestination(sensorRecord),sensorRecord, alertMessage), "text/html");
                }
            } catch (MessagingException ex) {
                throw new MessagingException("Error occurred setting the email content");
            }

//            try {
////                helper.addAttachment("cdbLogo", new ClassPathResource("templates/CdBLogo.webp"));
//                helper.addInline("cdbLogo", new ClassPathResource("templates/CdBLogo.webp"));
//            } catch (MessagingException ex) {
//                throw new MessagingException("Error occurred adding image to email template");
//            }

            try {
                mailSender.send(message);
            } catch (MailException ex) {
                throw new MessagingException("An error occurred sending the email : " + ex.getMessage());
            }


        }
        catch(Exception ex)
        {
            throw new MessagingException("A general error occurred sending the email : " + ex.getMessage());
        }
    }

    private String getRecipients(SensorRecord sr) throws IllegalArgumentException {
        if(sr.getAlertgroup().isEmpty()) {
            return null;
        }
        String alertGroup = sr.getAlertgroup();
        AlertContactGroup ag = AlertContactJSON.GetContactsForGroup(alertGroup).orElse(new AlertContactGroup());

        if(ag.getAddress().isEmpty()) {
            throw new java.lang.IllegalArgumentException("No recipients found for alertgroup:" + alertGroup);
        }
        log.debug("Email recipients : " + ag.getAddress());
        return (ag.getAddress());
    }
    private String getSubject(SensorRecord sr) {
        String subject = "Home Monitor Alert - " + sr.getTitle() + " - " + sr.getValue() + sr.getMeasurementUnit().toString();
        log.debug("Email Subject : " + subject);
        return subject;
    }

    private String getAlertDestination(SensorRecord sr) {
        String alertDestination = "";
        try {
            alertDestination = AlertDestination.valueOf(sr.getAlertdestination()).getTemplate();
        } catch (EnumConstantNotPresentException ex) {
            String validValues = AlertDestination.values().toString();
            log.error("The value of alertdestination in sensorlist.json for sensor : " + sr.getSensorid() + " is invalid.");
            log.error("Valid values are : " + validValues);
            alertDestination = "";
        }
        if (alertDestination == null || alertDestination.length() == 0) {
            log.error("No Alert Destination defined in sensorlist.json for sensor : " + sr.getSensorid());
            log.info("Using default Alert Template : AlertGeneralEmailTemplate.html");
            alertDestination = "AlertGeneralEmailTemplate.html";
        }

        return alertDestination;
    }

    private String getContent(String alertTemplate, SensorRecord sr, String alertMessage) {
        Context context = new Context();
        context.setVariable("temperature", sr.getValue().toString() + sr.getMeasurementUnit().toString());
        context.setVariable("date", sr.getMeasureTimeUTC());
        context.setVariable("message", alertMessage);
        log.debug("Email message Template : " + alertTemplate);
        log.debug("Email message text (optional)) : " + alertMessage);
        return templateEngine.process(alertTemplate, context);
    }
}
