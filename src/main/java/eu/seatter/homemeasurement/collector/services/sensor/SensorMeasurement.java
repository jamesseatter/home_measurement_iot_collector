package eu.seatter.homemeasurement.collector.services.sensor;

import eu.seatter.homemeasurement.collector.model.Sensor;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 30/01/2021
 * Time: 16:39
 */
public interface SensorMeasurement {
    List<Sensor> collect(List<Sensor> sensorList);
}
