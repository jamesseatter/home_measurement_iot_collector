package eu.seatter.homemeasurement.collector.sensor;

import eu.seatter.homemeasurement.collector.model.enums.SensorType;
import eu.seatter.homemeasurement.collector.sensor.types.AnalogueSensor;
import eu.seatter.homemeasurement.collector.sensor.types.DigitalSensor;
import eu.seatter.homemeasurement.collector.sensor.types.OneWirePi4JSensor;
import eu.seatter.homemeasurement.collector.sensor.types.Sensor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 09/12/2018
 * Time: 19:14
 */
@Slf4j
public class SensorFactory {
    private SensorFactory() {
    }

    public static @Nullable Sensor getSensor(SensorType sensorType) {
        if(sensorType == SensorType.ONEWIRE) {
            log.debug("SensorFactory returns a OneWirePi4JSensor reader");
            return new OneWirePi4JSensor();
        }
        if(sensorType == SensorType.ANALOGUE) {
            log.debug("SensorFactory returns an AnalogueSensor reader");
            return new AnalogueSensor();
        }
        if(sensorType == SensorType.DIGITAL) {
            log.debug("SensorFactory returns an DigitalSensor reader");
            return new DigitalSensor();
        }

        return null;
    }
}
