package eu.seatter.homemeasurement.collector.services.messaging;

import eu.seatter.homemeasurement.collector.model.SensorRecord;

import javax.mail.MessagingException;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 24/03/2019
 * Time: 12:13
 */
public interface SensorMessaging {
    void sendMeasurement(SensorRecord sensorRecord) throws MessagingException;
}
