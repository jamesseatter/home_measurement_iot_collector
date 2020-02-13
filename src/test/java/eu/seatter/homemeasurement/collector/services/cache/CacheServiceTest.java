package eu.seatter.homemeasurement.collector.services.cache;

import eu.seatter.homemeasurement.collector.cache.map.MeasurementCacheImpl;
import eu.seatter.homemeasurement.collector.model.SensorRecord;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.ZonedDateTime;
import java.util.*;

import static eu.seatter.homemeasurement.collector.TestUtility_SensorRecord.createTestRecord;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 21/05/2019
 * Time: 12:34
 */
@RunWith(SpringRunner.class)
public class CacheServiceTest {

    private SensorRecord sensorRecord1;
    private SensorRecord sensorRecord2;

    @TestConfiguration
    static class CacheServiceTestContextConfiguration {

        @Bean
        public MeasurementCacheService cacheService() {
            return new MeasurementCacheService();
        }
    }

    @Autowired
    private MeasurementCacheService cacheService;

    @MockBean
    private MeasurementCacheImpl measurementCache;


    @Before
    public void setUp() throws Exception {
        sensorRecord1 = createTestRecord("SENSOR_1", ZonedDateTime.now());
        sensorRecord2 = createTestRecord("SENSOR_2", ZonedDateTime.now());

    }

    @Test
    public void whenGetAll_thenReturnTwoSensors() {
        //given
        Map<String,List<SensorRecord>> sensorRecordMap = new HashMap<>();
        sensorRecordMap.putIfAbsent("SENSOR_1", Arrays.asList(sensorRecord1));
        sensorRecordMap.putIfAbsent("SENSOR_2", Arrays.asList(sensorRecord2));

        //when
        Mockito.when(measurementCache.getAll()).thenReturn(sensorRecordMap);

        //then
        assertEquals(2, cacheService.getAll().size());
        verify(measurementCache).getAll();
    }

    @Test
    public void whenGetAllBySensorId_thenReturnOneSensor() {
        //given
        List<SensorRecord> sensorRecordList = Arrays.asList(sensorRecord1);

        //when
        Mockito.when(measurementCache.getAllBySensorId(any(String.class))).thenReturn(sensorRecordList);

        //then
        assertEquals(1, cacheService.getAllBySensorId("SENSOR_1").size());
        verify(measurementCache).getAllBySensorId(any(String.class));
    }

    @Test
    public void getAllSorted() {
    }

    @Test
    public void whenGetLastBySensorId_thenReturn1Sensor() {
        //given
        List<SensorRecord> sensorRecordList = Arrays.asList(sensorRecord1);

        //when
        Mockito.when(measurementCache.getLastBySensorId(any(String.class),any(int.class))).thenReturn(sensorRecordList);

        //then
        assertEquals(1, cacheService.getLastBySensorId("SENSOR_1",1).size());
        verify(measurementCache).getLastBySensorId(any(String.class),any(int.class));
    }

    @Test
    public void whenGetSensorIds_thenReturn2Sensors() {
        //given
        ArrayList<String> sensorIds = new ArrayList<>();
        sensorIds.add("SENSOR_1");
        sensorIds.add("SENSOR_1");

        //when
        Mockito.when(measurementCache.getSensorIds()).thenReturn(sensorIds);

        //then
        assertEquals(2,cacheService.getSensorIds().size());
        verify(measurementCache).getSensorIds();
    }

    @Test
    public void whenGgetCacheMaxSizePerSensor_thenReturn24() {
        //when
        Mockito.when(measurementCache.getCacheMaxSizePerSensor()).thenReturn(24);

        //then
        assertEquals(24,cacheService.getCacheMaxSizePerSensor());
        verify(measurementCache).getCacheMaxSizePerSensor();
    }

    @Test
    public void whenGetCacheSizeBySensorId_returnOne() {
        //then
        Map<String,List<SensorRecord>> sensorRecordMap = new HashMap<>();
        sensorRecordMap.putIfAbsent("SENSOR_1", Arrays.asList(sensorRecord1));

        //when
        Mockito.when(measurementCache.getCacheSizeBySensorId(any(String.class))).thenReturn(1);

        //then
        assertEquals(1, cacheService.getCacheSizeBySensorId("SENSOR_1"));
        verify(measurementCache).getCacheSizeBySensorId(any(String.class));
    }
}