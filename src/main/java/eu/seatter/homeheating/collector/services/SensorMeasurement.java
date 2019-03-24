package eu.seatter.homeheating.collector.services;

import eu.seatter.homeheating.collector.model.SensorRecord;
import eu.seatter.homeheating.collector.sensor.Sensor;
import eu.seatter.homeheating.collector.sensor.SensorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 29/01/2019
 * Time: 17:13
 */
@Service
@Slf4j
public class SensorMeasurement {

    private Sensor sensorReader;

    public void collect(List<SensorRecord> sensorList) {
        log.info("Start measurement processing");
        for (SensorRecord sensorRecord : sensorList) {
            if(sensorRecord.getSensorID() == null) {
                continue;
            }
            SensorRecord srWithMeasurement;
            try {
                srWithMeasurement = readSensorValue(sensorRecord);
                log.debug(sensorRecord.loggerFormat() + " : Value returned - " + srWithMeasurement.getValue());
            } catch (Exception ex) {
                log.error(sensorRecord.loggerFormat() + " : Error reading sensor. " + ex.getMessage());
            }
            //todo Send measurement to edge
        }
        log.info("Completed measurement processing");
    }

    private SensorRecord readSensorValue(SensorRecord sensorRecord) {
        sensorReader = SensorFactory.getSensor(sensorRecord.getSensorType());
//        try {
            sensorRecord.setValue(sensorReader.readSensorData(sensorRecord));
            sensorRecord.setMeasureTime(LocalDateTime.now(ZoneOffset.UTC));
            log.debug(sensorRecord.loggerFormat() + " : Returned value - " + sensorRecord.getValue());
//        }
//        catch (RuntimeException ex) {
//            //todo improve exception handling
//            log.error(sensorRecord.loggerFormat() + " : " + ex.getLocalizedMessage());
//            throw ex;
//        }
        return sensorRecord;
    }
}
