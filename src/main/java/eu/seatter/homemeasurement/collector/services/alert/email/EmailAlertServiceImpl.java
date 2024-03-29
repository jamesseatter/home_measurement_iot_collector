package eu.seatter.homemeasurement.collector.services.alert.email;

import eu.seatter.homemeasurement.collector.model.enums.AlertDestination;
import eu.seatter.homemeasurement.collector.model.Sensor;
import eu.seatter.homemeasurement.collector.services.alert.AlertType;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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

    /**
     * @param alertType The type of alert being sent based on Enum AlertType
     * @param environment The execution environment, for example QA, Test, Prod
     * @param alertTitle The title of the alert message, subject line of the email
     * @param alertMessage The message that explains the alert in human readable format
     * @param measurement The Measurement object containing all the details of the measurement
     * @throws MessagingException The exception message
     */
    @Override
    public void sendAlert(AlertType alertType, String environment, String alertTitle, String alertMessage, Sensor measurement) throws MessagingException {

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
                if(alertType == AlertType.SYSTEM) {
                    message.setContent(getContent("AlertSystemMessageEmailTemplate", environment,alertTitle, measurement, alertMessage), "text/html");
                } else if(alertType == AlertType.MEASUREMENT) {
                    message.setContent(getContent(getAlertDestination(measurement), environment,alertTitle, measurement, alertMessage), "text/html");
                }
            } catch (MessagingException ex) {
                throw new MessagingException("Error occurred setting the email content");
            }

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


    private String getSubject(Sensor sr, @NotNull String alertTitle) {
        String subject = "Home Monitor Alert - " + sr.getTitle() + " - " + sr.getValue() + sr.getMeasurementUnit().toString();
        if(!alertTitle.equals("")) {
            subject = alertTitle;
        }
        log.debug("Email Subject : " + subject);
        return subject;
    }

    private String getAlertDestination(Sensor sr) {
        String alertDestination;
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
            case "test": return "Test";
            case "qa": return "Quality Assurance";
            case "prod": return "Production";
            case "dev":
            default:
                return "Developement";
        }

    }

    private String getContent(String alertTemplate, String environment, String title, Sensor sr, String alertMessage) {
        Context context = new Context();
        context.setVariable("environment", formatSpringEnvironment(environment));
        context.setVariable("title", title);
        context.setVariable("temperature", sr.getValue().toString() + sr.getMeasurementUnit().toString());
        context.setVariable("date", sr.getMeasureTimeUTC());
        context.setVariable("message", alertMessage);
        context.setVariable("sensortitle", sr.getTitle());
        log.debug("Email message Template : " + alertTemplate);
        log.debug("Email message text (optional)) : " + alertMessage);
        return templateEngine.process(alertTemplate, context);
    }
}
