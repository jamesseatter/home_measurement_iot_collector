package eu.seatter.homemeasurement.collector.services.alert;

import eu.seatter.homemeasurement.collector.model.SensorRecord;
import eu.seatter.homemeasurement.collector.services.alert.email.EmailAlertService;
import eu.seatter.homemeasurement.collector.services.alert.email.EmailAlertServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 06/06/2019
 * Time: 12:30
 */
@Service
@Slf4j
public class AlertServiceGeneralMessage implements AlertService {

    private final EmailAlertService emailAlertService;
    private final Boolean alertEmailEnabled;

    @Value("${spring.profiles.active:dev}") String applicationEnvironment;

    public AlertServiceGeneralMessage(EmailAlertServiceImpl emailAlertService, @Value("#{new Boolean('${message.alert.email.enabled:false}')}") Boolean alertEmailEnabled) {
        this.emailAlertService = emailAlertService;
        this.alertEmailEnabled = alertEmailEnabled;
    }

    @Override
    public void sendAlert(SensorRecord sensorRecord, String alertTitle, String alertMessage) throws MessagingException {
        if (alertTitle == null) alertTitle = "General Alert";
        if(alertEmailEnabled) {
            log.info("Email alerts enabled.");
            emailAlertService.sendAlert(AlertType.General, applicationEnvironment, alertTitle, alertMessage, sensorRecord);
        } else {
            log.info("Email alerts disabled.");
        }
    }
}
