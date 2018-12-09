package eu.seatter.homeheating.collector.Sensor;

import eu.seatter.homeheating.collector.Domain.SensorType;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 09/12/2018
 * Time: 19:14
 */
public class SensorFactory {
    public SensorReader getSensor(SensorType sensorType) {
        if(sensorType == SensorType.ONEWIRE) {
            return new SensorOneWirePi4J();
        }
        if(sensorType == SensorType.ANALOGUE) {
            return new SensorOneWirePi4J();
        }

        return null;
    }
}
