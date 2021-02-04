package eu.seatter.homemeasurement.collector.cache.map;

import eu.seatter.homemeasurement.collector.TestData;
import eu.seatter.homemeasurement.collector.model.Measurement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 04/02/2021
 * Time: 23:44
 */
class AlertalertMeasurementCacheMaplTest {

    private TestData testData = new TestData();
    private Measurement measurement1;
    private Measurement measurement1_2;
    private Measurement measurement2;

    private AlertMeasurementCacheMapImpl alertMeasurementCacheMap = new AlertMeasurementCacheMapImpl("test","test",24);

    @BeforeEach
    public void setUp() throws Exception {
        measurement1 = testData.getTestMeasurement("SENSOR_1", LocalDateTime.now());
        measurement1_2 = testData.getTestMeasurement("SENSOR_1", LocalDateTime.now().plusMinutes(1));
        measurement2 = testData.getTestMeasurement("SENSOR_2", LocalDateTime.now());
    }

    @Test
    public void whenGetAll_thenReturnTwoSensors() {
        //given
        alertMeasurementCacheMap.add(measurement1);
        alertMeasurementCacheMap.add(measurement2);

        //when

        //then
        assertEquals(2, alertMeasurementCacheMap.getAll().size());
    }

    @Test
    public void whenGetAllBySensorId_thenReturnOneSensor() {
        //given
        alertMeasurementCacheMap.add(measurement1);
        alertMeasurementCacheMap.add(measurement2);

        //when

        //then
        assertEquals(1, alertMeasurementCacheMap.getAllBySensorId("SENSOR_1").size());
    }

    @Test
    public void getAllSorted() {
    }

    @Test
    public void given2MeasurementsAddedForOneSensor_whenGetLastBySensorId_thenReturn1Measurement() {
        //given
        alertMeasurementCacheMap.add(measurement1);
        alertMeasurementCacheMap.add(measurement2);

        //then
        assertEquals(1, alertMeasurementCacheMap.getLastBySensorId("SENSOR_1",1).size());
        assertEquals(measurement1.getMeasureTimeUTC(), alertMeasurementCacheMap.getLastBySensorId("SENSOR_1",1).get(0).getMeasureTimeUTC());
    }

    @Test
    void givenCacheSetu10MaxEntriesPerSensor_whenGetCacheSizePerSensor_thenReturn10() {
        //given
        AlertMeasurementCacheMapImpl ac = new AlertMeasurementCacheMapImpl("test","test",10);

        //then
        assertEquals(10,ac.getCacheMaxSizePerSensor());
    }

    @Test
    public void whenGetCacheMaxSizePerSensor_thenReturn24() {
        //when

        //then
        assertEquals(24,alertMeasurementCacheMap.getCacheMaxSizePerSensor());

    }

    @Test
    public void whenGetCacheSizeBySensorId_returnOne() {
        //then
        alertMeasurementCacheMap.add(measurement1);
        alertMeasurementCacheMap.add(measurement1_2);

        //then
        assertEquals(2, alertMeasurementCacheMap.getCacheSizeBySensorId("SENSOR_1"));
    }
}