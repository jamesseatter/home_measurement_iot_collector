package eu.seatter.homemeasurement.collector;

import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.model.MeasurementUnit;
import eu.seatter.homemeasurement.collector.model.SensorType;

import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 21/05/2019
 * Time: 12:47
 */
public class TestUtility_Measurement {

    public static Measurement createTestRecord(String sensorId, LocalDateTime dt) {
        return new Measurement()
                .toBuilder()
                .alertdestination("PRIVATE")
                .alertgroup("testalertgroup")
                .description("Test sensor for testing json")
                .familyid(10)
                .high_threshold(70.0)
                .low_threshold(35.0)
                .measurementUnit(MeasurementUnit.C)
                .measureTimeUTC(dt)
                .sensorType(SensorType.ONEWIRE)
                .sensorid(sensorId)
                .title("Test Sensor Record")
                .value(55.8)
                .build();
    }
}
