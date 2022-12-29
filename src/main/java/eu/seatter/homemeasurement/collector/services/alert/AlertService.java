package eu.seatter.homemeasurement.collector.services.alert;

import eu.seatter.homemeasurement.collector.model.Sensor;
import eu.seatter.homemeasurement.collector.services.alert.email.EmailAlertService;
import eu.seatter.homemeasurement.collector.services.alert.email.EmailAlertServiceImpl;
import eu.seatter.homemeasurement.collector.services.alert.message.MessageAlertService;
import eu.seatter.homemeasurement.collector.services.alert.message.MessageAlertServiceImpl;
import eu.seatter.homemeasurement.collector.services.cache.AlertMeasurementCacheService;
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
    private final AlertMeasurementCacheService alertCacheService;
    private final boolean alertEmailEnabled;
    private final boolean alertMessagingEnabled;


    @Value("${spring.profiles.active:dev}") String applicationEnvironment;

    public AlertService(EmailAlertServiceImpl emailAlertService,
                        MessageAlertServiceImpl messageAlertService,
                        AlertMeasurementCacheService alertCacheService,
                        @Value("#{new Boolean('${message.alert.email.enabled:false}')}") boolean alertEmailEnabled,
                        @Value("#{new Boolean('${message.alert.messaging.enabled}')}") boolean alertMessagingEnabled) {
        this.emailAlertService = emailAlertService;
        this.messageAlertService = messageAlertService;
        this.alertCacheService = alertCacheService;
        this.alertEmailEnabled = alertEmailEnabled;
        this.alertMessagingEnabled = alertMessagingEnabled;
    }


    public void sendMeasurementAlert(Sensor measurement, String alertTitle, String alertMessage) throws MessagingException {
        alertCacheService.add(measurement);
        if(alertEmailEnabled) {
            log.info("Email alerts enabled.");
            emailAlertService.sendAlert(AlertType.MEASUREMENT, applicationEnvironment,alertTitle,alertMessage,measurement);
        } else {
            log.info("Email alerts disabled.");
        }
        if(alertMessagingEnabled) {
            log.info("Messaging alerts enabled.");
            messageAlertService.sendAlert(AlertType.MEASUREMENT, applicationEnvironment,alertTitle,alertMessage,measurement);
        } else {
            log.info("Messaging alerts disabled.");
        }
    }

    public void sendSystemAlert(String alertTitle, String alertMessage) throws MessagingException {
        if (alertTitle == null) alertTitle = "System Alert";
        if(alertEmailEnabled) {
            log.info("Email alerts enabled.");
            emailAlertService.sendAlert(AlertType.SYSTEM, applicationEnvironment, alertTitle, alertMessage, null);
        } else {
            log.info("Email alerts disabled.");
        }
        if(alertMessagingEnabled) {
            log.info("Messaging alerts enabled.");
            messageAlertService.sendAlert(AlertType.SYSTEM, applicationEnvironment,alertTitle,alertMessage,null);
        } else {
            log.info("Messaging alerts disabled.");
        }
    }

}
