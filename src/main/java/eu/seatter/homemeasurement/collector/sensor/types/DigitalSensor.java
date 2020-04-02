package eu.seatter.homemeasurement.collector.sensor.types;

import eu.seatter.homemeasurement.collector.model.Measurement;
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
public class DigitalSensor implements Sensor {

    @Override
    public Double readSensorData(Measurement measurement) {
        String sensorDescription = "sensor [" + measurement.getSensorid() + "/" + measurement.getSensorType() + "/" + measurement.getFamilyid() + "]";
        log.warn("DIGITAL SENSOR READING NOT IMPLEMENTED for sensor : " + sensorDescription);
        throw new RuntimeException("DIGITAL SENSOR NOT IMPLEMENTED");
        //return value;
    }
}
