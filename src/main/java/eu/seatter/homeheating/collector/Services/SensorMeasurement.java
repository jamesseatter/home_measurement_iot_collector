package eu.seatter.homeheating.collector.Services;

import eu.seatter.homeheating.collector.Domain.SensorRecord;
import eu.seatter.homeheating.collector.Sensor.Sensor;
import eu.seatter.homeheating.collector.Sensor.SensorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class SensorMeasurement {
    private Logger logger = LoggerFactory.getLogger(SensorMeasurement.class);

    private Sensor sensorReader;
    private String sensorDescription;

    public void collect(List<SensorRecord> sensorList) {
        logger.info("Start measurement processing");
        for (SensorRecord sensorRecord : sensorList) {
            SensorRecord srWithMeasurement;
            sensorDescription = "Sensor [" + sensorRecord.getSensorID() + "/" + sensorRecord.getSensorType() + "/" + sensorRecord.getFamilyId() + "]";
            logger.info(sensorDescription);
            try {
                srWithMeasurement = readSensorValue(sensorRecord);
                logger.debug(sensorDescription + " : Value returned - " + srWithMeasurement.getValue());
            } catch (Exception ex) {
                logger.error("Error reading sensor with ID: " + sensorRecord.getSensorID() + ". " + ex.getMessage());
            }
            //todo Send to edge
        }
        logger.info("Completed measurement processing");
    }

    private SensorRecord readSensorValue(SensorRecord sensorRecord) {
        sensorReader = SensorFactory.getSensor(sensorRecord.getSensorType());
        logger.debug(sensorDescription + " : Reading value");
        try {
            sensorRecord.setValue(sensorReader.readSensorData(sensorRecord));
            sensorRecord.setMeasureTime(LocalDateTime.now(ZoneOffset.UTC));
            logger.debug(sensorDescription + " : Returned value - " + sensorRecord.getValue());
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        return sensorRecord;
    }
}
