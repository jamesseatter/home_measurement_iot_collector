package eu.seatter.homemeasurement.collector.sensor;

import eu.seatter.homemeasurement.collector.model.SensorType;
import eu.seatter.homemeasurement.collector.sensor.types.AnalogueSensor;
import eu.seatter.homemeasurement.collector.sensor.types.DigitalSensor;
import eu.seatter.homemeasurement.collector.sensor.types.OneWirePi4JSensor;
import eu.seatter.homemeasurement.collector.sensor.types.Sensor;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 09/12/2018
 * Time: 19:14
 */
public class SensorFactory {
    public static Sensor getSensor(SensorType sensorType) {
        if(sensorType == SensorType.ONEWIRE) {
            return new OneWirePi4JSensor();
        }
        if(sensorType == SensorType.ANALOGUE) {
            return new AnalogueSensor();
        }
        if(sensorType == SensorType.DIGITAL) {
            return new DigitalSensor();
        }

        return null;
    }
}
