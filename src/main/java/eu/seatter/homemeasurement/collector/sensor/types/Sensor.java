package eu.seatter.homemeasurement.collector.sensor.types;

import eu.seatter.homemeasurement.collector.model.SensorRecord;

import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 07/12/2018
 * Time: 13:25
 */
public interface Sensor {

    String sensorDescription="";

    Optional<Double> readSensorData(SensorRecord sensorRecord);
}
