package eu.seatter.homemeasurement.collector.cache.map;


import eu.seatter.homemeasurement.collector.TestData;
import eu.seatter.homemeasurement.collector.model.Sensor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 21/05/2019
 * Time: 12:34
 */
class MeasurementCacheMapImplTest {
    private final TestData testData = new TestData();
    private Sensor measurement1;
    private Sensor measurement2;
    private final MeasurementCacheMapImpl measurementCacheMapImp = new MeasurementCacheMapImpl("test","test",10,14);

    @BeforeEach
    void setUp() {
        measurement1 = testData.getTestMeasurement("SENSOR_1", LocalDateTime.now());
        measurement2 = testData.getTestMeasurement("SENSOR_2", LocalDateTime.now());
    }

    @Test
    void whenGetAll_thenReturnTwoSensors() {
        //given
        measurementCacheMapImp.add(measurement1,true);
        measurementCacheMapImp.add(measurement2,true);

        //when

        //then
        assertEquals(2, measurementCacheMapImp.getAll().size());
    }
}