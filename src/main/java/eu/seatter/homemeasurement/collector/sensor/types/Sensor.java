package eu.seatter.homemeasurement.collector.sensor.types;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 07/12/2018
 * Time: 13:25
 */
public interface Sensor {
    Double readSensorData(eu.seatter.homemeasurement.collector.model.Sensor measurement);
}
