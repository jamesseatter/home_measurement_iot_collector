package eu.seatter.homemeasurement.collector.services.alert.message;

import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.model.MeasurementAlert;
import eu.seatter.homemeasurement.collector.model.SystemAlert;
import eu.seatter.homemeasurement.collector.services.alert.AlertType;
import eu.seatter.homemeasurement.collector.services.alert.email.EmailAlertGroupRecipientService;
import eu.seatter.homemeasurement.collector.services.messaging.RabbitMQService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 31/03/2020
 * Time: 12:30
 */
@Service
@Slf4j
public class MessageAlertServiceImpl implements MessageAlertService {
    final RabbitMQService rabbitMQService;
    private final EmailAlertGroupRecipientService emailAlertGroupRecipientService;

    public MessageAlertServiceImpl(RabbitMQService rabbitMQService, EmailAlertGroupRecipientService emailAlertGroupRecipientService) {
        this.rabbitMQService = rabbitMQService;
        this.emailAlertGroupRecipientService = emailAlertGroupRecipientService;
    }

    @Override
    public void sendAlert(AlertType alertType, String environment, String alertTitle, String alertMessage, Measurement measurement) throws MessagingException {
        if(alertType == AlertType.Measurement) {
            MeasurementAlert measurementAlert = convertmeasurementToMeasurementAlert(measurement);
            measurementAlert.setTitle(alertTitle);
            measurementAlert.setMessage(alertMessage);
            measurementAlert.setEnvironment(environment);

            rabbitMQService.sendMeasurementAlert(measurementAlert);

        } else if(alertType == AlertType.System) {
            SystemAlert systemAlert = new SystemAlert();
            systemAlert.setAlertUID(UUID.randomUUID());
            systemAlert.setAlertTimeUTC(ZonedDateTime.now(ZoneId.of("Etc/UTC")).truncatedTo(ChronoUnit.SECONDS));
            systemAlert.setTitle(alertTitle);
            systemAlert.setMessage(alertMessage);

            rabbitMQService.sendSystemAlert(systemAlert);
        }
    }

    private MeasurementAlert convertmeasurementToMeasurementAlert(Measurement sr) {
        MeasurementAlert ma = new MeasurementAlert();
        if(sr != null) {
            ma.setAlertTimeUTC(sr.getMeasureTimeUTC());
            ma.setTitle(sr.getTitle());
            ma.setValue(sr.getValue());
            ma.setMeasurementUnit(sr.getMeasurementUnit());
            ma.setAlertSentEmailTO(sr.getAlertgroup());
            ma.setAlertUID(sr.getRecordUID());
            ma.setAlertSentEmailTO(emailAlertGroupRecipientService.getRecipients(sr.getAlertgroup()));
            return ma;
        }
        return null;
    }

}
