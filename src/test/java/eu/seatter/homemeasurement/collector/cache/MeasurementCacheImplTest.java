//package eu.seatter.homemeasurement.collector.cache;
//
//import eu.seatter.homemeasurement.collector.cache.map.MeasurementCacheMapImpl;
//import eu.seatter.homemeasurement.collector.model.Measurement;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.ExpectedException;
//
//import java.time.ZonedDateTime;
//import java.util.List;
//import java.util.Map;
//
//import static eu.seatter.homemeasurement.collector.TestUtility_Measurement.createTestRecord;
//import static org.junit.Assert.assertEquals;
//
///**
// * Created by IntelliJ IDEA.
// * User: jas
// * Date: 06/05/2019
// * Time: 15:50
// */
//public class MeasurementCacheImplTest {
//
//    MeasurementCache measurementCache;
//    private int MAX_ENTRIES_PER_SENSOR =24;
//
//    @Before
//    public void setUp() {
//        this.measurementCache = new MeasurementCacheMapImpl();
//        this.MAX_ENTRIES_PER_SENSOR = this.measurementCache.getCacheMaxSizePerSensor();
//    }
//
//
//
//    @Test
//    public void givenAddOneEntry_thenGetAllReturnsOne() {
//        //given
//        Measurement sr = createTestRecord("28-0000000001", ZonedDateTime.now());
//        measurementCache.add(sr);
//
//        //when
//        List<Measurement> result = measurementCache.getAllBySensorId(sr.getSensorid());
//
//        //then
//        assertEquals(result.size(),1);
//        assertEquals(sr.getSensorid(),result.get(0).getSensorid());
//    }
//
//    @Test
//    public void givenAddTwoCacheEntries_thenReturnTwoEntries() {
//        //given
//        Measurement sr = createTestRecord("28-0000000001", ZonedDateTime.now());
//        measurementCache.add(sr);
//
//        Measurement sr1 = createTestRecord("28-0000000001", ZonedDateTime.now());
//        measurementCache.add(sr1);
//
//        //when
//        List<Measurement> result = measurementCache.getAllBySensorId(sr1.getSensorid());
//
//        //then
//        assertEquals(result.size(),2);
//    }
//
//    @Test
//    public void givenAddRecords_whenAddMoreThanCacheMaxEntries_thenCacheSizeEqualsCacheMax() {
//        //given
//        String sensorId = "28-000000000";
//        Measurement sr;
//        for (int i = 0; i < MAX_ENTRIES_PER_SENSOR + 5 ; i++) {
//            sr = createTestRecord(sensorId,ZonedDateTime.now());
//            measurementCache.add(sr);
//        }
//
//        //when
//        int cacheSize = measurementCache.getCacheSizeBySensorId(sensorId);
//        List<Measurement> records = measurementCache.getAllBySensorId(sensorId);
//
//        //then
//        assertEquals(cacheSize, MAX_ENTRIES_PER_SENSOR);
//        assertEquals(records.size(), MAX_ENTRIES_PER_SENSOR);
//    }
//
//    @Test
//    public void whenCacheEmpty_thenReturnEmptySetOfSensorIds() {
//        //given
//
//        //when
//        List<String> result = measurementCache.getSensorIds();
//
//        //then
//        assertEquals(result.size(),0);
//    }
//
//    @Test
//    public void whenMeasurementCacheConstructorSizeSet_thenCacheMaxSizeIsCorrect() {
//        //given
//
//        //when
//        int cacheMaxSize = measurementCache.getCacheMaxSizePerSensor();
//
//        //then
//        assertEquals(cacheMaxSize, MAX_ENTRIES_PER_SENSOR);
//    }
//
//    @Test
//    public void givenTwoSensorsAdded_thenGetSensorIdsReturnsTwoSensors() {
//        //given
//        Measurement sr;
//        for (int i = 0 ; i < 2 ; i++) {
//            sr = createTestRecord("28-000000000" + i, ZonedDateTime.now());
//            measurementCache.add(sr);
//        }
//
//        //when
//        int sensorCount = measurementCache.getSensorIds().size();
//        List<String> records = measurementCache.getSensorIds();
//
//        //then
//        assertEquals(sensorCount,2);
//        assertEquals(records.size(),2);
//
//    }
//
//    @Test
//    public void givenTenRecordsAdded_whenLast5Requested_thenReturnLast5Records() {
//        //given
//        int requestedCount = 5;
//        String sensorId = "28-000000000";
//        Measurement sr;
//        for (int i = 0 ; i < 10 ; i++) {
//            sr = createTestRecord(sensorId,ZonedDateTime.now());
//            sr.setValue((double)i);
//            measurementCache.add(sr);
//        }
//
//        //when
//        List<Measurement> results = measurementCache.getLastBySensorId(sensorId,requestedCount);
//
//        //then
//        assertEquals(results.size(), requestedCount);
//        assertEquals(0.0, (results.get(results.size()-1).getValue()),0);
//    }
//
//    @Test
//    public void givenTenRecordsAdded_whenLastNoValueRequested_thenReturnLast1Records() {
//        //given
//        String sensorId = "28-000000000";
//        Measurement sr;
//        for (int i = 0 ; i < 10 ; i++) {
//            sr = createTestRecord(sensorId,ZonedDateTime.now());
//            sr.setValue((double)i);
//            measurementCache.add(sr);
//        }
//
//        //when
//        List<Measurement> results = measurementCache.getLastBySensorId(sensorId,1);
//
//        //then
//        assertEquals(results.size(),1);
//        assertEquals(0.0, (results.get(0).getValue()),0);
//    }
//
//    @Rule
//    public ExpectedException exceptionRule = ExpectedException.none();
//
//    @Test
//    public void givenNoRecords_whenGetLastBySensorId_thenAssertIllegalArgumentException() {
//        //given
//        String sensorId="BADSENSORID";
//        exceptionRule.expect(IllegalArgumentException.class);
//        exceptionRule.expectMessage("The sensor, " + sensorId + " has no records in the cache");
//
//
//
//        //when
//        measurementCache.getLastBySensorId(sensorId,1);
//
//        //then
//    }
//
//    @Test
//    public void givenFiveRecords_whenGetLastBySensorIdTenRecords_thenAssertIllegalArgumentException() {
//        //given
//        int recordCount = 5;
//        int requestCount = 10;
//        String sensorId="BADSENSORID";
//        exceptionRule.expect(IllegalArgumentException.class);
//        exceptionRule.expectMessage("The number of values requested, " + requestCount + ", is greater then the number of records cached for the sensor " + recordCount);
//
//        Measurement sr;
//        for (int i = 0 ; i < recordCount ; i++) {
//            sr = createTestRecord(sensorId,ZonedDateTime.now());
//            sr.setValue((double)i);
//            measurementCache.add(sr);
//        }
//
//        //when
//        measurementCache.getLastBySensorId(sensorId,requestCount);
//
//        //then
//    }
//
//    @Test
//    public void add() {
//    }
//
//    @Test
//    public void getAll() {
//    }
//
//    @Test
//    public void getAllBySensorId() {
//    }
//
//    @Test
//    public void givenAddRecords_thenReturnSortedByTime() {
//        String sensorId = "28-000000000";
//        String sensorId2 = "30-000000000";
//        Measurement sr;
//        measurementCache.add(createTestRecord(sensorId,ZonedDateTime.now().withHour(10).withMinute(10)));
//        measurementCache.add(createTestRecord(sensorId2,ZonedDateTime.now().withHour(10).withMinute(10)));
//        measurementCache.add(createTestRecord(sensorId,ZonedDateTime.now().withHour(10).withMinute(15)));
//        measurementCache.add(createTestRecord(sensorId2,ZonedDateTime.now().withHour(10).withMinute(15)));
//        measurementCache.add(createTestRecord(sensorId,ZonedDateTime.now().withHour(10).withMinute(20)));
//        measurementCache.add(createTestRecord(sensorId2,ZonedDateTime.now().withHour(10).withMinute(20)));
//        measurementCache.add(createTestRecord(sensorId,ZonedDateTime.now().withHour(10).withMinute(05)));
//        measurementCache.add(createTestRecord(sensorId2,ZonedDateTime.now().withHour(10).withMinute(05)));
//        measurementCache.add(createTestRecord(sensorId,ZonedDateTime.now().withHour(10).withMinute(25)));
//        measurementCache.add(createTestRecord(sensorId2,ZonedDateTime.now().withHour(10).withMinute(25)));
//        measurementCache.add(createTestRecord(sensorId,ZonedDateTime.now().withHour(10).withMinute(30)));
//        measurementCache.add(createTestRecord(sensorId2,ZonedDateTime.now().withHour(10).withMinute(30)));
//Measurement sr22 = createTestRecord(sensorId,ZonedDateTime.now().withHour(10).withMinute(10));
//        //then
//        Map<String, List<Measurement>> measurements = measurementCache.getAllSorted();
//        assertEquals(30, measurements.get("30-000000000").get(0).getMeasureTimeUTC().getMinute());
//        assertEquals(5, measurements.get("30-000000000").get(measurements.get("30-000000000").size()-1).getMeasureTimeUTC().getMinute());
//    }
//
//    @Test
//    public void getLastBySensorId() {
//    }
//
//    @Test
//    public void getSensorIds() {
//    }
//
//    @Test
//    public void getCacheMaxSizePerSensor() {
//    }
//
//    @Test
//    public void getCacheSizeBySensorId() {
//    }
//}