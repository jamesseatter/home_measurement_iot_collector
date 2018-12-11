package eu.seatter.homeheating.collector.Sensor;

import eu.seatter.homeheating.collector.Domain.SensorRecord;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 07/12/2018
 * Time: 13:25
 */
public interface Sensor {

    Double readSensorData(SensorRecord sensorRecord);
}
