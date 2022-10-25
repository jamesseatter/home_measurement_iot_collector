package eu.seatter.homemeasurement.collector.services.sensor;

import eu.seatter.homemeasurement.collector.exception.SensorNotFoundException;
import eu.seatter.homemeasurement.collector.exception.SensorValueException;
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
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

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


    public List<Measurement> collect(@NotNull List<Measurement> sensorList) {
        final List<Measurement> measurements = new ArrayList<>();
        log.info("Start measurement collection");

        LocalDateTime measurementTime = getTimeDateNowInUTC(); //all measurements will use the same time to make reporting easier.
        for (Measurement measurement : sensorList) {
            if(measurement.getSensorid() == null) {
                log.error(measurement.loggerFormat() + " : SensorId not found");
            } else {
                try {
                    readSensorValue(measurement);
                    measurement.setRecordUID(java.util.UUID.randomUUID());
                    measurement.setMeasureTimeUTC(measurementTime);
                    measurement.setRecordUID(UUID.randomUUID());
                    log.debug(measurement.loggerFormat() + " : Value returned - " + measurement.getValue());

                    log.debug(measurement.loggerFormat() + " : Sensor measurementadjustmentvalue = " + measurement.getMeasurementadjustmentvalue());
                    measurement.setValue(measurement.getValue() + measurement.getMeasurementadjustmentvalue());
                    measurements.add(measurement);
                }
                catch (SensorNotFoundException ex) {
                    String message = measurement.loggerFormat() + " : Error reading sensor - " + measurement.getSensorid() + " / " + measurement.getTitle();
                    String alertMessage = "The sensor did not respond to a query, this may be a transient fault or there may be a problem with the sensor. If this occurs twice verify the sensor is still attached to the system.";
                    String exceptionMessage = measurement.loggerFormat() + " : " + ex.getLocalizedMessage();
                    sensorList.remove(measurement);
                    sendAlert(message,alertMessage,exceptionMessage);
                }
                catch (SensorValueException ex) {
                    String message = measurement.loggerFormat() + " : Error reading sensor - " + measurement.getSensorid() + " / " + measurement.getTitle();
                    String alertMessage = "The sensor returned an illegal value, this may be a transient fault or there may be a problem with the sensor. If this occurs twice verify the sensor is still attached and working.";
                    String exceptionMessage = measurement.loggerFormat() + " : " + ex.getLocalizedMessage();
                    sensorList.remove(measurement);
                    sendAlert(message,alertMessage,exceptionMessage);
                }
                catch (Exception ex) {
                    String message = measurement.loggerFormat() + " : General error occurred - " + measurement.getSensorid() + " / " + measurement.getTitle();
                    String alertMessage = "General error occurred - " + measurement.getSensorid() + " / " + measurement.getTitle();
                    String exceptionMessage = measurement.loggerFormat() + " : " + ex.getLocalizedMessage();
                    sensorList.remove(measurement);
                    sendAlert(message,alertMessage,exceptionMessage);
                }
            }
            log.info(measurement.loggerFormat() + " : Updated sensor measurement = " + measurement.getValue());
        }
        log.info("Completed measurement collection");
        return measurements;
    }

    private void readSensorValue(@NotNull Measurement measurement) throws SensorNotFoundException, SensorValueException {
        Sensor sensorReader;
        try {
            sensorReader = SensorFactory.getSensor(measurement.getSensortype());
        } catch (Exception ex) {
            log.error("Unable to create a sensorReader for sensor :" + measurement.getSensorid() + " " + measurement.getSensortype());
            throw ex;
        }
        int counter = 1;
        int maxCounter = 3;
        measurement.setValue(0.0);
        while (counter <= maxCounter) {
            measurement.setValue(sensorReader.readSensorData(measurement));
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
            try {
                Thread.sleep(5000);
            }  catch (InterruptedException ex) {
                log.error(measurement.loggerFormat() + " : " + ex.getLocalizedMessage());
                log.error(measurement.loggerFormat() + " : " + Arrays.toString(ex.getStackTrace()));
                measurement.setValue(0.0);
                Thread.currentThread().interrupt();
            }
            counter++;
        }
        if(measurement.getValue() == 0.0d) throw new SensorValueException("Sensor measurement failed","The value returned by the sensor is illegal and has been ignored. Check the sensor is connected and working.");
    }

    private LocalDateTime getTimeDateNowInUTC() {
        return LocalDateTime.now().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime().truncatedTo(ChronoUnit.MINUTES);
    }

    private void sendAlert(String message, String alertMessage, String exceptionMessage) {
        log.error(message);
        log.error(alertMessage);
        log.error(exceptionMessage);

        try {
            alertService.sendSystemAlert(message, alertMessage);
            alertSystemCacheService.add("Sensor Measurement", message);
        } catch (MessagingException exM) {
            log.error("Error sending Alert");
        }
    }
}
