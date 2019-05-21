package eu.seatter.homemeasurement.collector.controllers;

import eu.seatter.homemeasurement.collector.cache.map.MeasurementCacheImpl;
import eu.seatter.homemeasurement.collector.model.SensorMeasurementUnit;
import eu.seatter.homemeasurement.collector.model.SensorRecord;
import eu.seatter.homemeasurement.collector.model.SensorType;
import eu.seatter.homemeasurement.collector.services.cache.CacheService;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 16/05/2019
 * Time: 08:23
 */
public class DashboardControllerTest {

    private CacheService cacheService;

    @Before
    public void setUp() {
        this.cacheService = new CacheService(new MeasurementCacheImpl());
    }

    @Test
    public void givenMeasurementList_ReturnMVC() {
        //given
        cacheService.add(createTestRecord("SENSOR_2", LocalDateTime.of(2019,01,01,12,01)));
        cacheService.add(createTestRecord("SENSOR_1", LocalDateTime.of(2019,01,01,18,01)));
        cacheService.add(createTestRecord("SENSOR_2", LocalDateTime.of(2019,01,01,05,01)));
        cacheService.add(createTestRecord("SENSOR_3", LocalDateTime.of(2019,01,01,01,01)));
        cacheService.add(createTestRecord("SENSOR_2", LocalDateTime.of(2019,01,01,18,01)));
        cacheService.add(createTestRecord("SENSOR_3", LocalDateTime.of(2019,01,01,18,01)));
        cacheService.add(createTestRecord("SENSOR_1", LocalDateTime.of(2019,01,01,12,01)));
        cacheService.add(createTestRecord("SENSOR_3", LocalDateTime.of(2019,01,01,12,01)));
        cacheService.add(createTestRecord("SENSOR_1", LocalDateTime.of(2019,01,01,05,01)));
        cacheService.add(createTestRecord("SENSOR_2", LocalDateTime.of(2019,01,01,01,01)));
        cacheService.add(createTestRecord("SENSOR_1", LocalDateTime.of(2019,01,01,01,01)));
        cacheService.add(createTestRecord("SENSOR_3", LocalDateTime.of(2019,01,01,05,01)));

        //when


        //then

    }

    private SensorRecord createTestRecord(String sensorId,LocalDateTime dt) {
        return new SensorRecord()
                .toBuilder()
                .familyid(10).description("Test sensor for testing json").low_threshold(35.0).high_threshold(70.0).measurementUnit(SensorMeasurementUnit.C).sensorType(SensorType.ONEWIRE).value(55.8).alertgroup("testalertgroup")
                .sensorid(sensorId)
                .measureTimeUTC(dt)
                .build();
    }
}