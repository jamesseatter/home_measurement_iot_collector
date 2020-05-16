package eu.seatter.homemeasurement.collector.services;

import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.model.MeasurementUnit;
import eu.seatter.homemeasurement.collector.model.SensorType;
import eu.seatter.homemeasurement.collector.utils.UtilDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 12/05/2020
 * Time: 21:26
 */
@Component
@Slf4j
public class TestData {
    public static List<Measurement> testSensorList() {
        log.warn("Test sensor list in use");
        List<Measurement> list = new ArrayList<>();
//        Measurement sr = new Measurement();
        list.add(Measurement.builder()
                .recordUID(UUID.randomUUID())
                .sensorid("28-000000000001")
                .title("Température de l'eau à l'arrivée")
                .description("Returns the temperature of the hot water entering the house from the central heating system")
                .familyid(40)
                .sensorType(SensorType.ONEWIRE)
                .low_threshold(45.0)
                .high_threshold(60.0)
                .alertgroup("temperature_threshold_alerts_private")
                .alertdestination("BORRY")
//                .alertSentToMQ(false)
//                .alertSent_MeasurementTolerance(false)
//                .measurementSentToMq(false)
                .build());

        list.add(Measurement.builder()
                .recordUID(UUID.randomUUID())
                .sensorid("28-000000000002")
                .title("Température de l'eau de chaudière")
                .description("Returns the temperature of the hot water in the boiler")
                .familyid(40)
                .sensorType(SensorType.ONEWIRE)
                .low_threshold(35.0)
                .high_threshold(60.0)
                .alertgroup("temperature_threshold_alerts_private")
                .alertdestination("PRIVATE")
//                .alertSentToMQ(false)
//                .alertSent_MeasurementTolerance(false)
//                .measurementSentToMq(false)
                .build());

        return list;
    }

    public static List<Measurement> testData(List<Measurement> sensorList) {
        log.warn("Test measurement data in use");
        for(Measurement srec : sensorList) {
            srec.setMeasurementUnit(MeasurementUnit.C);
            srec.setMeasureTimeUTC(UtilDateTime.getTimeDateNowNoSecondsInUTC());
            int val = ThreadLocalRandom.current().nextInt(35, 75);
            srec.setValue((double)val);
        }

        return Collections.unmodifiableList(sensorList);
    }
}
