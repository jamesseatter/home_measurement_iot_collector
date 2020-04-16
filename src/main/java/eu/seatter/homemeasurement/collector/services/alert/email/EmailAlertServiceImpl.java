package eu.seatter.homemeasurement.collector.services.alert.email;

import eu.seatter.homemeasurement.collector.model.AlertDestination;
import eu.seatter.homemeasurement.collector.model.Measurement;
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
import java.util.Arrays;

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
    private final EmailAlertGroupRecipientService emailAlertGroupRecipientService;

    public EmailAlertServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine, EmailAlertGroupRecipientService emailAlertGroupRecipientService) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.emailAlertGroupRecipientService = emailAlertGroupRecipientService;
    }

    @Override
    public void sendAlert(AlertType alertType, String environment, String alertTitle, String alertMessage, Measurement measurement) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "utf-8");
        try {
            try {
                String recipients = emailAlertGroupRecipientService.getRecipients(measurement.getAlertgroup());
                if(recipients != null) {
                    helper.setTo(recipients.split(";"));
                }
            } catch (MessagingException ex) {
                throw new MessagingException("Error adding mail recipients to mailer message");
            }

            try {
                helper.setSubject(getSubject(measurement, alertTitle));
            } catch (MessagingException ex) {
                throw new MessagingException("No email recipients defined");
            }

            try {
                //todo Cleanup the templates, this is now a system alert.
                if(alertType == AlertType.System) {
                    message.setContent(getContent("AlertSystemMessageEmailTemplate", environment,alertTitle, measurement, alertMessage), "text/html");
                } else if(alertType == AlertType.Measurement) {
                    message.setContent(getContent(getAlertDestination(measurement), environment,alertTitle, measurement, alertMessage), "text/html");
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


    private String getSubject(Measurement sr, String alertTitle) {
        String subject = "Home Monitor Alert - " + sr.getTitle() + " - " + sr.getValue() + sr.getMeasurementUnit().toString();
        if(alertTitle != null || !alertTitle.equals("")) {
            subject = alertTitle;
        }
        log.debug("Email Subject : " + subject);
        return subject;
    }

    private String getAlertDestination(Measurement sr) {
        String alertDestination = "";
        try {
            alertDestination = AlertDestination.valueOf(sr.getAlertdestination()).getTemplate();
        } catch (EnumConstantNotPresentException ex) {
            String validValues = Arrays.toString(AlertDestination.values());
            log.error("The value of alertdestination in sensorlist.json for sensor : " + sr.getSensorid() + " is invalid.");
            log.error("Valid values are : " + validValues);
            alertDestination = "";
        }
        if (alertDestination == null || alertDestination.length() == 0) {
            log.error("No Alert Destination defined in sensorlist.json for sensor : " + sr.getSensorid());
            log.info("Using default Alert Template : AlertSystemEmailTemplate.html");
            alertDestination = "AlertSystemEmailTemplate.html";
        }

        return alertDestination;
    }

    private String formatSpringEnvironment(String environment) {
        switch (environment) {
            case "dev": return "Developement";
            case "test": return "Test";
            case "qa": return "Quality Assurance";
            case "prod": return "Production";
        }
        return "Developement";
    }

    private String getContent(String alertTemplate, String environment, String title, Measurement sr, String alertMessage) {
        Context context = new Context();
        context.setVariable("environment", formatSpringEnvironment(environment));
        context.setVariable("title", title);
        context.setVariable("temperature", sr.getValue().toString() + sr.getMeasurementUnit().toString());
        context.setVariable("date", sr.getMeasureTimeUTC());
        context.setVariable("message", alertMessage);
        log.debug("Email message Template : " + alertTemplate);
        log.debug("Email message text (optional)) : " + alertMessage);
        return templateEngine.process(alertTemplate, context);
    }
}
