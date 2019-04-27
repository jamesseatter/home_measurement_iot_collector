package eu.seatter.homemeasurement.collector.services.sensor;

import eu.seatter.homemeasurement.collector.model.SensorRecord;
import eu.seatter.homemeasurement.collector.sensor.SensorFactory;
import eu.seatter.homemeasurement.collector.sensor.types.Sensor;
import eu.seatter.homemeasurement.collector.services.messaging.EmailAlertService;
import eu.seatter.homemeasurement.collector.services.messaging.MailMessageAlertMeasurement;
import eu.seatter.homemeasurement.collector.services.messaging.Messaging;
import eu.seatter.homemeasurement.collector.services.messaging.RabbitMQService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
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

    private final EmailAlertService alertService;

    private Sensor sensorReader;
    private final Messaging mqService;
    private final boolean mqEnabled;

    @Value("${measurement.temperature.alert.threshold:50}")
    private double temperatureAlertThreshold;

    public SensorMeasurement(RabbitMQService mqService, @Value("${RabbitMQService.enabled:false}") boolean enabled, EmailAlertService alertService) {
        this.mqService = mqService;
        this.mqEnabled = enabled;
        this.alertService = alertService;
    }

    public void collect(List<SensorRecord> sensorList) {
        log.info("Start measurement processing");
        for (SensorRecord sensorRecord : sensorList) {
            if(sensorRecord.getSensorid() == null) {
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

            validateMeasurementThreshold(sensorRecord);
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

    private void validateMeasurementThreshold(SensorRecord sensorRecord) {
        //todo move threshold into a sensor config file so that thresholds are per sensor.
        if(sensorRecord.getValue() <= temperatureAlertThreshold) {
            log.debug("Sensor value below threshold. Measurement : " + sensorRecord.getValue() + " / Threshold : " + temperatureAlertThreshold);
            try {
                log.info("Sending alert email to " + "***REMOVED***");
                alertService.sendAlert(new MailMessageAlertMeasurement(sensorRecord));
            } catch (MessagingException e) {
                log.error("Failed to send Email Alert : " + e.getLocalizedMessage());
            }
        }

    }
}
