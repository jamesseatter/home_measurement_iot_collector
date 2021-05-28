package eu.seatter.homemeasurement.collector.services.sensor;

import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.sensor.SensorFactory;
import eu.seatter.homemeasurement.collector.sensor.types.Sensor;
import eu.seatter.homemeasurement.collector.services.alert.AlertService;
import eu.seatter.homemeasurement.collector.services.cache.AlertSystemCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 29/01/2019
 * Time: 17:13
 */
@Service
@Slf4j
@Primary
@Profile("!dev")
public class SensorMeasurementImpl implements SensorMeasurement {

    private final AlertService alertService;
    private final AlertSystemCacheService alertSystemCacheService;

    public SensorMeasurementImpl(AlertService alertService, AlertSystemCacheService alertSystemCache) {
        this.alertService = alertService;
        this.alertSystemCacheService = alertSystemCache;
    }


    public List<Measurement> collect(List<Measurement> sensorList) {
        final List<Measurement> measurements = new ArrayList<>();
        log.info("Start measurement collection");

        LocalDateTime measurementTime = getTimeDateNowInUTC(); //all measurements will use the same time to make reporting easier.
        for (Measurement measurement : sensorList) {
            if(measurement.getSensorid() == null) {
                log.error(measurement.loggerFormat() + " : SensorId not found");
            } else {
                readSensorValue(measurement);
                measurement.setRecordUID(java.util.UUID.randomUUID());
                measurement.setMeasureTimeUTC(measurementTime);
                measurement.setRecordUID(UUID.randomUUID());
                measurements.add(measurement);
                log.debug(measurement.loggerFormat() + " : Value returned - " + measurement.getValue());
                if(measurement.getValue().intValue() == 0) {
                    String message = measurement.loggerFormat() + " : Error reading sensor - " + measurement.getSensorid() + " / " + measurement.getTitle();
                    log.error(message);
                    try {
                        alertService.sendSystemAlert(message, "The sensor did not respond to a query, this may be a transient fault or there may be a problem with the sensor. If this occurs twice verify the sensor is still attached to the system.");
                        alertSystemCacheService.add("Sensor Measurement", message);
                    } catch (MessagingException exM) {
                        log.error(measurement.loggerFormat() + " : " + exM.getLocalizedMessage());
                    }
                }
                log.debug(measurement.loggerFormat() + " : Sensor measurementadjustmentvalue = " + measurement.getMeasurementadjustmentvalue());
                measurement.setValue(measurement.getValue()+measurement.getMeasurementadjustmentvalue());
            }
            log.info(measurement.loggerFormat() + " : Updated sensor measurement = " + measurement.getValue());
        }
        log.info("Completed measurement collection");
        return measurements;
    }

    private void readSensorValue(Measurement measurement) {
        Sensor sensorReader = SensorFactory.getSensor(measurement.getSensorType());
        int counter = 1;
        int maxCounter = 3;
        measurement.setValue(0.0);
        try {
            while (counter <= maxCounter) {
                measurement.setValue(Objects.requireNonNull(sensorReader).readSensorData(measurement));
                if ((measurement.getValue().intValue() == 85) || (measurement.getValue().intValue() == 0) || (measurement.getValue().isNaN())) {
                    log.warn(measurement.loggerFormat() + " : Sensor returned " + measurement.getValue() + " which indicates a reading error. Retry (" + counter + " of " + maxCounter + ")");
                    measurement.setValue(0.0);
                }
                else if ((measurement.getValue() > 100) || (measurement.getValue().intValue() < 0)) {
                    log.warn(measurement.loggerFormat() + " : Sensor returned " + measurement.getValue() + " which may be an error. Retry (" + counter + " of " + maxCounter + ")");
                    measurement.setValue(0.0);
                } else {
                    //Good measurement value
                    break;
                }
                Thread.sleep(5000);
                counter++;
            }
        }
        catch (RuntimeException | InterruptedException ex) {
            log.error(measurement.loggerFormat() + " : " + ex.getLocalizedMessage());
            measurement.setValue(0.0);
            Thread.currentThread().interrupt();
        }
    }

    private LocalDateTime getTimeDateNowInUTC() {
        return LocalDateTime.now().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime().truncatedTo(ChronoUnit.MINUTES);
    }
}
