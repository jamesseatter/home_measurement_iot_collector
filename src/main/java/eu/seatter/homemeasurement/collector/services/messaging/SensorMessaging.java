package eu.seatter.homemeasurement.collector.services.messaging;

import eu.seatter.homemeasurement.collector.model.SensorRecord;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 24/03/2019
 * Time: 12:13
 */
public interface SensorMessaging {
    void sendMeasurement(SensorRecord sensorRecord);
    //void registerDevice(Device device);
    //void registerSensor(Sensor sensor);
}
