package eu.seatter.homemeasurement.collector.services.sensor;

import eu.seatter.homemeasurement.collector.model.Sensor;
import eu.seatter.homemeasurement.collector.model.enums.MeasurementUnit;
import eu.seatter.homemeasurement.collector.utils.UtilDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 30/01/2021
 * Time: 16:40
 */
@Service
@Slf4j
@Profile("dev")
public class SensorMeasurementDev implements SensorMeasurement {
    @Override
    public List<Sensor> collect(List<Sensor> sensorList) {
        log.warn("Test measurement data in use");
        for(Sensor srec : sensorList) {
            srec.setRecordUID(java.util.UUID.randomUUID());
            srec.setMeasurementUnit(MeasurementUnit.C);
            srec.setMeasureTimeUTC(UtilDateTime.getTimeDateNowNoSecondsInUTC());
            int val = ThreadLocalRandom.current().nextInt(35, 75);
            srec.setValue((double)val);
        }

        return Collections.unmodifiableList(sensorList);
    }
}
