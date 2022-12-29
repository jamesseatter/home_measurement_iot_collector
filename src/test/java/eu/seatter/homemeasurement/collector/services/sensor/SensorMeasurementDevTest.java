package eu.seatter.homemeasurement.collector.services.sensor;

import eu.seatter.homemeasurement.collector.TestData;
import eu.seatter.homemeasurement.collector.model.Sensor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 02/02/2021
 * Time: 09:24
 */
class SensorMeasurementDevTest {
    SensorMeasurementDev sensorMeasurementDev = new SensorMeasurementDev();
    final TestData testData = new TestData();

    @Test
    void givenTestSensorsThanMeasuementsGreaterthanZeroReturned() {
        //given
        List<Sensor> sensorList = testData.getTestSensorList();
        List<Sensor> measurements = testData.getTestMeasurements(sensorList);

        //when

        //than
        for (Sensor m: measurements) {
            assertNotNull(m.getValue());
            assertTrue(m.getValue()>0.0);
        }
    }

}