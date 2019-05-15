package eu.seatter.homemeasurement.collector.services.alert.email;

import eu.seatter.homemeasurement.collector.model.SensorRecord;

import javax.mail.MessagingException;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 13/05/2019
 * Time: 16:24
 */
public interface EmailAlertService {

    void sendAlert(SensorRecord sensorRecord) throws MessagingException;
}
