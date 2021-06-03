package eu.seatter.homemeasurement.collector.cache.map;


import eu.seatter.homemeasurement.collector.TestData;
import eu.seatter.homemeasurement.collector.model.Measurement;
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
    private Measurement measurement1;
    private Measurement measurement1_2;
    private Measurement measurement2;

    private final MeasurementCacheMapImpl measurementCacheMapImp = new MeasurementCacheMapImpl("test","test",10);

    @BeforeEach
    void setUp() {
        measurement1 = testData.getTestMeasurement("SENSOR_1", LocalDateTime.now());
        measurement1_2 = testData.getTestMeasurement("SENSOR_1", LocalDateTime.now().plusMinutes(1));
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

    @Test
    void whenGetAllBySensorId_thenReturnOneSensor() {
        //given
        measurementCacheMapImp.add(measurement1,true);
        measurementCacheMapImp.add(measurement2,true);

        //when

        //then
        assertEquals(1, measurementCacheMapImp.getAllBySensorId("SENSOR_1").size());
    }

    @Test
    void given2MeasurementsAddedForOneSensor_whenGetLastBySensorId_thenReturn1Measurement() {
        //given
        measurementCacheMapImp.add(measurement1,true);
        measurementCacheMapImp.add(measurement2,true);

        //then
        assertEquals(1, measurementCacheMapImp.getLastBySensorId("SENSOR_1",1).size());
        assertEquals(measurement1.getMeasureTimeUTC(), measurementCacheMapImp.getLastBySensorId("SENSOR_1",1).get(0).getMeasureTimeUTC());
    }

    @Test
    void givenCacheSetu10MaxEntriesPerSensor_whenGetCacheSizePerSensor_thenReturn10() {
        //given
        MeasurementCacheMapImpl mc = new MeasurementCacheMapImpl("test","test",10);

        //then
        assertEquals(10,mc.getCacheMaxSizePerSensor());
    }

    @Test
    void whenGetCacheMaxSizePerSensor_thenReturn24() {
        //when

        //then
        assertEquals(10,measurementCacheMapImp.getCacheMaxSizePerSensor());

    }

    @Test
    void whenGetCacheSizeBySensorId_returnOne() {
        //then
        measurementCacheMapImp.add(measurement1,true);
        measurementCacheMapImp.add(measurement1_2,true);

        //then
        assertEquals(2, measurementCacheMapImp.getCacheSizeBySensorId("SENSOR_1"));
    }
}