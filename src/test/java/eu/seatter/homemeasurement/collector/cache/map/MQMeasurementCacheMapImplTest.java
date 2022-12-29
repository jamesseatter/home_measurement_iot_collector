package eu.seatter.homemeasurement.collector.cache.map;

import eu.seatter.homemeasurement.collector.TestData;
import eu.seatter.homemeasurement.collector.model.Sensor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 04/02/2021
 * Time: 23:34
 */
class MQMeasurementCacheMapImplTest {
    private final TestData testData = new TestData();
    private Sensor measurement1;
    private Sensor measurement1_2;
    private Sensor measurement2;

    private final MQMeasurementCacheMapImpl mqMeasurementCacheMap = new MQMeasurementCacheMapImpl("test","test");

    @BeforeEach
    public void setUp() throws Exception {
        measurement1 = testData.getTestMeasurement("SENSOR_1", LocalDateTime.now());
        measurement1_2 = testData.getTestMeasurement("SENSOR_1", LocalDateTime.now().plusMinutes(1));
        measurement2 = testData.getTestMeasurement("SENSOR_2", LocalDateTime.now());
    }

    @Test
    void given3RecordsAdded_then3RecordsInCache() {
        //given
        mqMeasurementCacheMap.add(measurement1);
        mqMeasurementCacheMap.add(measurement1_2);
        mqMeasurementCacheMap.add(measurement2);

        //then
        assertEquals(3,mqMeasurementCacheMap.getAll().size());
    }

    @Test
    void given3RecordsAdded_whenRemove1_then2RecordsInCache() {
        //given
        mqMeasurementCacheMap.add(measurement1);
        mqMeasurementCacheMap.add(measurement1_2);
        mqMeasurementCacheMap.add(measurement2);

        //when
        mqMeasurementCacheMap.remove(measurement2);

        //then
        assertEquals(2,mqMeasurementCacheMap.getAll().size());
    }

    @Test
    void given3RecordsAdded_whenRemoveAllSensor1_then1RecordsInCache() {
        //given
        List<Sensor> sensor1 = new ArrayList<>();
        sensor1.add(measurement1);
        sensor1.add(measurement1_2);

        mqMeasurementCacheMap.add(measurement1);
        mqMeasurementCacheMap.add(measurement1_2);
        mqMeasurementCacheMap.add(measurement2);

        //when
        mqMeasurementCacheMap.removeAll(sensor1);

        //then
        assertEquals(1,mqMeasurementCacheMap.getAll().size());
    }

    @Test
    void given3RecordsAdded_whenGetAll_then2RecordsInCache() {
        //given
        mqMeasurementCacheMap.add(measurement1);
        mqMeasurementCacheMap.add(measurement1_2);
        mqMeasurementCacheMap.add(measurement2);

        //then
        assertEquals(3,mqMeasurementCacheMap.getAll().size());
    }

    @Test
    void given3RecordsAdded_whenGetCacheSize_then2RecordsInCache() {
        //given
        mqMeasurementCacheMap.add(measurement1);
        mqMeasurementCacheMap.add(measurement1_2);
        mqMeasurementCacheMap.add(measurement2);

        //then
        assertEquals(3,mqMeasurementCacheMap.getCacheSize());
    }
}