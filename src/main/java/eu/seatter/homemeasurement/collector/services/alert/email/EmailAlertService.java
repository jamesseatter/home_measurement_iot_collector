package eu.seatter.homemeasurement.collector.services.alert.email;

import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.services.alert.AlertType;

import javax.mail.MessagingException;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 13/05/2019
 * Time: 16:24
 */
public interface EmailAlertService {
    void sendAlert(AlertType alertType, String environment, String alertTitle, String alertMessage, Measurement measurement) throws MessagingException;
}
