package eu.seatter.homemeasurement.collector.services.sensor;

import eu.seatter.homemeasurement.collector.model.SensorRecord;
import eu.seatter.homemeasurement.collector.sensor.SensorFactory;
import eu.seatter.homemeasurement.collector.sensor.types.Sensor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 29/01/2019
 * Time: 17:13
 */
@Service
@Slf4j
public class SensorMeasurement {

    private final List<SensorRecord> measurements = new ArrayList<>();

    public List<SensorRecord> collect(List<SensorRecord> sensorList) {
        log.info("Start measurement collection");
        LocalDateTime measurementTime = LocalDateTime.now(ZoneOffset.UTC).withSecond(00); //all measurements will use the same time to make reporting easier.
        for (SensorRecord sensorRecord : sensorList) {
            if(sensorRecord.getSensorid() == null) {
                log.error(sensorRecord.loggerFormat() + " : SensorId not found");
                //todo improve handling of missing sensorId
                continue;
            }
            SensorRecord srWithMeasurement;
            try {
                readSensorValue(sensorRecord);
                sensorRecord.setMeasureTimeUTC(measurementTime);
                measurements.add(sensorRecord);
                log.debug(sensorRecord.loggerFormat() + " : Value returned - " + sensorRecord.getValue());
            } catch (Exception ex) {
                log.error(sensorRecord.loggerFormat() + " : Error reading sensor. " + ex.getMessage());
                break;
            }
        }
        log.info("Completed measurement collection");
        return measurements;
    }

    private void readSensorValue(SensorRecord sensorRecord) {
        Sensor sensorReader = SensorFactory.getSensor(sensorRecord.getSensorType());
        try {
            sensorRecord.setValue(Objects.requireNonNull(sensorReader).readSensorData(sensorRecord));

        }
        catch (RuntimeException ex) {
            //todo improve exception handling
            log.error(sensorRecord.loggerFormat() + " : " + ex.getLocalizedMessage());
            throw ex;
        }
    }


}
