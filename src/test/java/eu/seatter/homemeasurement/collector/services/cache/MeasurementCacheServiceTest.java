package eu.seatter.homemeasurement.collector.services.cache;


import eu.seatter.homemeasurement.collector.TestData;
import eu.seatter.homemeasurement.collector.cache.map.MeasurementCacheMapImpl;
import eu.seatter.homemeasurement.collector.model.Measurement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 21/05/2019
 * Time: 12:34
 */
@ExtendWith(MockitoExtension.class)
class MeasurementCacheServiceTest {
    private final TestData testData = new TestData();
    private Measurement measurement1;
    private Measurement measurement2;

    @InjectMocks
    private MeasurementCacheService measurementCacheService;

    @Mock
    private MeasurementCacheMapImpl measurementCache;

    @BeforeEach
    public void init(){

    }

    @BeforeEach
    public void setUp() {
        measurement1 = testData.getTestMeasurement("SENSOR_1", LocalDateTime.now());
        measurement2 = testData.getTestMeasurement("SENSOR_2", LocalDateTime.now());

    }

    @Test
    void givenAdd1_whenGetall_thenCacheServiceGetallHas1(){
        //given
        Map<String,List<Measurement>> measurementMap = new HashMap<>();
        measurementMap.putIfAbsent("SENSOR_1", Collections.singletonList(measurement1));
        measurementCacheService.add(measurement1);

        //when
        when(measurementCacheService.getAll()).thenReturn(measurementMap);

        //then
        assertEquals(1,measurementCacheService.getAll().size());
        verify(measurementCache).add(ArgumentMatchers.any(Measurement.class),ArgumentMatchers.anyBoolean());
        verify(measurementCache).getAll();
    }

    @Test
    void givenAdd2_when_getall_thenCacheServiceGetallHas2() {
        //given
        Map<String,List<Measurement>> measurementMap = new HashMap<>();
        measurementMap.putIfAbsent("SENSOR_1", Collections.singletonList(measurement1));
        measurementMap.putIfAbsent("SENSOR_2", Collections.singletonList(measurement2));

        measurementCacheService.add(measurement1);

        //when
        when(measurementCacheService.getAll()).thenReturn(measurementMap);

        //then
        assertEquals(2, measurementCacheService.getAll().size());
        verify(measurementCache).add(ArgumentMatchers.any(Measurement.class),ArgumentMatchers.anyBoolean());
        verify(measurementCache).getAll();
    }

    @Test
    void givenAdd2_whenGetallsorted_thenCacheServiceGetallsortedHas2() {
        //given
        Map<String,List<Measurement>> measurementMap = new HashMap<>();
        measurementMap.putIfAbsent("SENSOR_1", Collections.singletonList(measurement1));
        measurementMap.putIfAbsent("SENSOR_2", Collections.singletonList(measurement2));

        measurementCacheService.add(measurement1);

        Map<String,List<Measurement>> measurementMapSorted = new HashMap<>();
        measurementMapSorted.putIfAbsent("SENSOR_1", Collections.singletonList(measurement1));
        measurementMapSorted.putIfAbsent("SENSOR_2", Collections.singletonList(measurement2));

        //when
        when(measurementCacheService.getAllSorted()).thenReturn(measurementMapSorted);

        //then
        assertEquals(2,measurementCacheService.getAllSorted().size());
        verify(measurementCache,times(1)).add(ArgumentMatchers.any(Measurement.class),ArgumentMatchers.anyBoolean());
        verify(measurementCache, times(1)).getAllSorted();
    }

    @Test
    void whenFlushToFile_thenReturnTrue() throws IOException {
        //given

        //when
        when(measurementCacheService.flushToFile()).thenReturn(true);

        //then
        assertTrue(measurementCacheService.flushToFile());
    }
}