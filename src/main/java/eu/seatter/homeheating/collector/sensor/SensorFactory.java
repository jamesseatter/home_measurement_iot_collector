package eu.seatter.homeheating.collector.sensor;

import eu.seatter.homeheating.collector.model.SensorType;

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
            return new OneWirePi4JSensor();
        }

        return null;
    }
}
