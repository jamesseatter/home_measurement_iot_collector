package eu.seatter.homemeasurement.collector.services.alert;

import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.services.alert.email.EmailAlertService;
import eu.seatter.homemeasurement.collector.services.alert.email.EmailAlertServiceImpl;
import eu.seatter.homemeasurement.collector.services.alert.message.MessageAlertService;
import eu.seatter.homemeasurement.collector.services.alert.message.MessageAlertServiceImpl;
import eu.seatter.homemeasurement.collector.services.cache.AlertCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 31/03/2020
 * Time: 21:43
 */
@Service
@Slf4j
public class AlertService {
    private final EmailAlertService emailAlertService;
    private final MessageAlertService messageAlertService;
    private final AlertCacheService alertCacheService;
    private final Boolean alertEmailEnabled;
    private final Boolean alertMessagingEnabled;


    @Value("${spring.profiles.active:dev}") String applicationEnvironment;

    public AlertService(EmailAlertServiceImpl emailAlertService,
                        MessageAlertServiceImpl messageAlertService,
                        AlertCacheService alertCacheService,
                        @Value("#{new Boolean('${message.alert.email.enabled:false}')}") Boolean alertEmailEnabled,
                        @Value("#{new Boolean('${message.alert.messaging.enabled}')}") Boolean alertMessagingEnabled) {
        this.emailAlertService = emailAlertService;
        this.messageAlertService = messageAlertService;
        this.alertCacheService = alertCacheService;
        this.alertEmailEnabled = alertEmailEnabled;
        this.alertMessagingEnabled = alertMessagingEnabled;
    }


    public void sendMeasurementAlert(Measurement measurement, String alertTitle, String alertMessage) throws MessagingException {
        alertCacheService.add(measurement);
        if(alertEmailEnabled) {
            log.info("Email alerts enabled.");
            emailAlertService.sendAlert(AlertType.Measurement, applicationEnvironment,alertTitle,alertMessage,measurement);
            //measurement.setAlertSent_MeasurementTolerance(true);
        } else {
            log.info("Email alerts disabled.");
        }
        if(alertMessagingEnabled) {
            log.info("Messaging alerts enabled.");
            messageAlertService.sendAlert(AlertType.Measurement, applicationEnvironment,alertTitle,alertMessage,measurement);
            //measurement.setAlertSent_MeasurementTolerance(true);
        } else {
            log.info("Messaging alerts disabled.");
        }
    }

    public void sendSystemAlert(String alertTitle, String alertMessage) throws MessagingException {
        if (alertTitle == null) alertTitle = "System Alert";
        if(alertEmailEnabled) {
            log.info("Email alerts enabled.");
            emailAlertService.sendAlert(AlertType.System, applicationEnvironment, alertTitle, alertMessage, null);
        } else {
            log.info("Email alerts disabled.");
        }
        if(alertMessagingEnabled) {
            log.info("Messaging alerts enabled.");
            messageAlertService.sendAlert(AlertType.System, applicationEnvironment,alertTitle,alertMessage,null);
            //measurement.setAlertSent_MeasurementTolerance(true);
        } else {
            log.info("Messaging alerts disabled.");
        }
    }

}
