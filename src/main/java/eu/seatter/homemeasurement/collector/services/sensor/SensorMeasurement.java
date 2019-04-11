package eu.seatter.homemeasurement.collector.services.sensor;

import eu.seatter.homemeasurement.collector.model.SensorRecord;
import eu.seatter.homemeasurement.collector.sensor.types.Sensor;
import eu.seatter.homemeasurement.collector.sensor.SensorFactory;
import eu.seatter.homemeasurement.collector.services.messaging.RabbitMQService;
import eu.seatter.homemeasurement.collector.services.messaging.Messaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private Messaging mqService;
    private boolean mqEnabled;

    public SensorMeasurement(RabbitMQService mqService, @Value("${RabbitMQService.enabled:false}") boolean enabled) {
        this.mqService = mqService;
        this.mqEnabled = enabled;
    }

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
                break;
            }
            //todo Send measurement to edge
            // multiple options are possible simultaneously based on them being enabled in Application.properties
            if(mqEnabled){
                mqService.sendMeasurement(srWithMeasurement);
            }
        }
        log.info("Completed measurement processing");
    }

    private SensorRecord readSensorValue(SensorRecord sensorRecord) {
        sensorReader = SensorFactory.getSensor(sensorRecord.getSensorType());
        try {
            sensorRecord.setValue(sensorReader.readSensorData(sensorRecord));
            sensorRecord.setMeasureTimeUTC(LocalDateTime.now(ZoneOffset.UTC));
        }
        catch (RuntimeException ex) {
            //todo improve exception handling
            log.error(sensorRecord.loggerFormat() + " : " + ex.getLocalizedMessage());
            throw ex;
        }
        return sensorRecord;
    }
}
