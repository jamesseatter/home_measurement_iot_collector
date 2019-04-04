package eu.seatter.homemeasurement.collector.sensor.types;

import eu.seatter.homemeasurement.collector.model.SensorRecord;
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
    private Double value=0D;
    private String sensorID;
    private String sensorDescription;

    @Override
    public Double readSensorData(SensorRecord sensorRecord) {
        sensorDescription = "sensor [" + sensorRecord.getSensorID() + "/" + sensorRecord.getSensorType() + "/" + sensorRecord.getFamilyId() + "]";
        log.warn("DIGITAL SENSOR READING NOT IMPLEMENTED for sensor : " + sensorDescription);
        throw new RuntimeException("DIGITAL SENSOR NOT IMPLEMENTED");
        //return value;
    }
}
