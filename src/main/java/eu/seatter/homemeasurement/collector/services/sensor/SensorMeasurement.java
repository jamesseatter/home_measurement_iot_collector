package eu.seatter.homemeasurement.collector.services.sensor;

import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.sensor.SensorFactory;
import eu.seatter.homemeasurement.collector.sensor.types.Sensor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
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
public class SensorMeasurement {

    private final List<Measurement> measurements = new ArrayList<>();

    @SuppressWarnings("OctalInteger")
    public List<Measurement> collect(List<Measurement> sensorList) {
        log.info("Start measurement collection");
        ZonedDateTime measurementTime = ZonedDateTime.now(ZoneId.of("Etc/UTC")).truncatedTo(ChronoUnit.MINUTES); //all measurements will use the same time to make reporting easier.
        for (Measurement measurement : sensorList) {
            if(measurement.getSensorid() == null) {
                log.error(measurement.loggerFormat() + " : SensorId not found");
                //todo improve handling of missing sensorId
                continue;
            }
            Measurement srWithMeasurement;
            try {
                readSensorValue(measurement);
                measurement.setMeasureTimeUTC(measurementTime);
                measurement.setRecordUID(UUID.randomUUID());
                measurements.add(measurement);
                log.debug(measurement.loggerFormat() + " : Value returned - " + measurement.getValue());
            } catch (Exception ex) {
                log.error(measurement.loggerFormat() + " : Error reading sensor. " + ex.getMessage());
                break;
            }
        }
        log.info("Completed measurement collection");
        return measurements;
    }

    private void readSensorValue(Measurement measurement) {
        Sensor sensorReader = SensorFactory.getSensor(measurement.getSensorType());
        try {
            measurement.setValue(Objects.requireNonNull(sensorReader).readSensorData(measurement));
        }
        catch (RuntimeException ex) {
            //todo improve exception handling
            log.error(measurement.loggerFormat() + " : " + ex.getLocalizedMessage());
            throw ex;
        }
    }
}
