package eu.seatter.homemeasurement.collector.cache;

import eu.seatter.homemeasurement.collector.model.SensorMeasurementUnit;
import eu.seatter.homemeasurement.collector.model.SensorRecord;
import eu.seatter.homemeasurement.collector.model.SensorType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 06/05/2019
 * Time: 15:50
 */
public class MeasurementCacheImplTest {

    MeasurementCache measurementCache;
    private int MAX_ENTRIES_PER_SENSOR =24;

    @Before
    public void setUp() {
        this.measurementCache = new MeasurementCacheImpl();
        this.MAX_ENTRIES_PER_SENSOR = this.measurementCache.getCacheMaxSizePerSensor();
    }

    private SensorRecord testRecord(String sensorId) {
        SensorRecord sr = new SensorRecord();
        sr.setFamilyid(40);
        sr.setSensorid(sensorId);
        sr.setSensorType(SensorType.ONEWIRE);
        sr.setMeasureTimeUTC(LocalDateTime.now());
        sr.setMeasurementUnit(SensorMeasurementUnit.C);
        sr.setValue(20.0);

        return sr;
    }

    @Test
    public void givenAddOneEntry_thenGetAllReturnsOne() {
        //given
        SensorRecord sr = testRecord("28-0000000001");
        measurementCache.add(sr);

        //when
        List<SensorRecord> result = measurementCache.getAllBySensorId(sr.getSensorid());

        //then
        assertEquals(result.size(),1);
        assertEquals(sr.getSensorid(),result.get(0).getSensorid());
    }

    @Test
    public void givenAddTwoCacheEntries_thenReturnTwoEntries() {
        //given
        SensorRecord sr = testRecord("28-0000000001");
        measurementCache.add(sr);

        SensorRecord sr1 = testRecord("28-0000000001");
        measurementCache.add(sr1);

        //when
        List<SensorRecord> result = measurementCache.getAllBySensorId(sr1.getSensorid());

        //then
        assertEquals(result.size(),2);
    }

    @Test
    public void givenAddRecords_whenAddMoreThanCacheMaxEntries_thenCacheSizeEqualsCacheMax() {
        //given
        String sensorId = "28-000000000";
        SensorRecord sr;
        for (int i = 0; i < MAX_ENTRIES_PER_SENSOR + 5 ; i++) {
            sr = testRecord(sensorId);
            measurementCache.add(sr);
        }

        //when
        int cacheSize = measurementCache.getCacheSizeBySensorId(sensorId);
        List<SensorRecord> records = measurementCache.getAllBySensorId(sensorId);

        //then
        assertEquals(cacheSize, MAX_ENTRIES_PER_SENSOR);
        assertEquals(records.size(), MAX_ENTRIES_PER_SENSOR);
    }

    @Test
    public void whenCacheEmpty_thenReturnEmptySetOfSensorIds() {
        //given

        //when
        Set<String> result = measurementCache.getSensorIds();

        //then
        assertEquals(result.size(),0);
    }

    @Test
    public void whenMeasurementCacheConstructorSizeSet_thenCacheMaxSizeIsCorrect() {
        //given

        //when
        int cacheMaxSize = measurementCache.getCacheMaxSizePerSensor();

        //then
        assertEquals(cacheMaxSize, MAX_ENTRIES_PER_SENSOR);
    }

    @Test
    public void givenTwoSensorsAdded_thenGetSensorIdsReturnsTwoSensors() {
        //given
        SensorRecord sr;
        for (int i = 0 ; i < 2 ; i++) {
            sr = testRecord("28-000000000" + i);
            measurementCache.add(sr);
        }

        //when
        int sensorCount = measurementCache.getSensorIds().size();
        Set<String> records = measurementCache.getSensorIds();

        //then
        assertEquals(sensorCount,2);
        assertEquals(records.size(),2);

    }

    @Test
    public void givenTenRecordsAdded_whenLast5Requested_thenReturnLast5Records() {
        //given
        int requestedCount = 5;
        String sensorId = "28-000000000";
        SensorRecord sr;
        for (int i = 0 ; i < 10 ; i++) {
            sr = testRecord(sensorId);
            sr.setValue((double)i);
            measurementCache.add(sr);
        }

        //when
        List<SensorRecord> results = measurementCache.getLastBySensorId(sensorId,requestedCount);

        //then
        assertEquals(results.size(), requestedCount);
        assertEquals(9.0, (results.get(results.size()-1).getValue()),0);
    }

    @Test
    public void givenTenRecordsAdded_whenLastNoValueRequested_thenReturnLast1Records() {
        //given
        String sensorId = "28-000000000";
        SensorRecord sr;
        for (int i = 0 ; i < 10 ; i++) {
            sr = testRecord(sensorId);
            sr.setValue((double)i);
            measurementCache.add(sr);
        }

        //when
        List<SensorRecord> results = measurementCache.getLastBySensorId(sensorId);

        //then
        assertEquals(results.size(),1);
        assertEquals(9.0, (results.get(0).getValue()),0);
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void givenNoRecords_whenGetLastBySensorId_thenAssertIllegalArgumentException() {
        //given
        String sensorId="BADSENSORID";
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("The sensor, " + sensorId + " has no records in the cache");



        //when
        measurementCache.getLastBySensorId(sensorId);

        //then
    }

    @Test
    public void givenFiveRecords_whenGetLastBySensorIdTenRecords_thenAssertIllegalArgumentException() {
        //given
        int recordCount = 5;
        int requestCount = 10;
        String sensorId="BADSENSORID";
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("The number of values requested, " + requestCount + ", is greater then the number of records cached for the sensor " + recordCount);

        SensorRecord sr;
        for (int i = 0 ; i < recordCount ; i++) {
            sr = testRecord(sensorId);
            sr.setValue((double)i);
            measurementCache.add(sr);
        }

        //when
        measurementCache.getLastBySensorId(sensorId,requestCount);

        //then
    }

}