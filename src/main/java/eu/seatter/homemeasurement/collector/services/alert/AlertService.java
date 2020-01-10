package eu.seatter.homemeasurement.collector.services.alert;

import eu.seatter.homemeasurement.collector.model.SensorRecord;

import javax.mail.MessagingException;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 12/04/2019
 * Time: 13:13
 */
public interface AlertService {
    void sendAlert(SensorRecord sensorRecord, String alertMessage) throws MessagingException;
}
