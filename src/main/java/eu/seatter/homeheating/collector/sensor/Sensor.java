package eu.seatter.homeheating.collector.sensor;

import eu.seatter.homeheating.collector.domain.SensorRecord;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 07/12/2018
 * Time: 13:25
 */
public interface Sensor {

    String sensorDescription="";

    Double readSensorData(SensorRecord sensorRecord);
}
