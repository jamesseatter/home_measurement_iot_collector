package eu.seatter.homemeasurement.collector.services.alert.message;

import eu.seatter.homemeasurement.collector.model.Sensor;
import eu.seatter.homemeasurement.collector.services.alert.AlertType;

import javax.mail.MessagingException;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 31/03/2020
 * Time: 12:29
 */
public interface MessageAlertService {
    void sendAlert(AlertType alertType, String environment, String alertTitle, String alertMessage, Sensor measurement) throws MessagingException;
}
