package eu.seatter.homemeasurement.collector.services.messaging;

import eu.seatter.homemeasurement.collector.model.Sensor;
import eu.seatter.homemeasurement.collector.model.MeasurementAlert;
import eu.seatter.homemeasurement.collector.model.SystemAlert;

import javax.mail.MessagingException;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 24/03/2019
 * Time: 12:13
 */
public interface SensorMessaging {
    boolean sendMeasurement(Sensor measurement);
    boolean sendMeasurementAlert(MeasurementAlert measurementAlert) throws MessagingException;
    boolean sendSystemAlert(SystemAlert systemAlert);
    boolean flushCache();
}
