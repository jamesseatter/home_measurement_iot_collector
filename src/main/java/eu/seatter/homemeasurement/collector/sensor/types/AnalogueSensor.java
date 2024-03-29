package eu.seatter.homemeasurement.collector.sensor.types;

import eu.seatter.homemeasurement.collector.model.Sensor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 07/12/2018
 * Time: 21:13
 */
@Slf4j
@Component
public class AnalogueSensor implements eu.seatter.homemeasurement.collector.sensor.types.Sensor {

    @Override
    public Double readSensorData(Sensor measurement) {
        Double value=0D;
        String sensorDescription = "sensor [" + measurement.getSensorid() + "/" + measurement.getSensortype() + "/" + measurement.getFamilyid() + "]";
        log.warn("ANALOGUE SENSOR READING NOT IMPLEMENTED for sensor : " + sensorDescription);
        return value;
    }
}
