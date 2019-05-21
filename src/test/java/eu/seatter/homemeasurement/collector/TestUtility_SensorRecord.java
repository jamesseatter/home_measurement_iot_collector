package eu.seatter.homemeasurement.collector;

import eu.seatter.homemeasurement.collector.model.SensorMeasurementUnit;
import eu.seatter.homemeasurement.collector.model.SensorRecord;
import eu.seatter.homemeasurement.collector.model.SensorType;

import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 21/05/2019
 * Time: 12:47
 */
public class TestUtility_SensorRecord {

    public static SensorRecord createTestRecord(String sensorId, LocalDateTime dt) {
        return new SensorRecord()
                .toBuilder()
                .familyid(10).description("Test sensor for testing json").low_threshold(35.0).high_threshold(70.0).measurementUnit(SensorMeasurementUnit.C).sensorType(SensorType.ONEWIRE).value(55.8).alertgroup("testalertgroup")
                .sensorid(sensorId)
                .measureTimeUTC(dt)
                .build();
    }
}
