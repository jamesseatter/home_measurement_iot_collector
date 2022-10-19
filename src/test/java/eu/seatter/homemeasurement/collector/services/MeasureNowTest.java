package eu.seatter.homemeasurement.collector.services;

import eu.seatter.homemeasurement.collector.TestData;
import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.model.MeasurementWeb;
import eu.seatter.homemeasurement.collector.services.sensor.SensorListService;
import eu.seatter.homemeasurement.collector.services.sensor.SensorMeasurement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 08/02/2021
 * Time: 22:28
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class MeasureNowTest {
    @Mock private SensorListService sensorListServiceMock;
    @Mock private SensorMeasurement sensorMeasurementMock;
    @InjectMocks private MeasureNow measureNow;

    private final TestData testData = new TestData();

    @BeforeAll
    void init() {

    }

    @Test
    void givenSensorList_when2Measurements_thenReturn2Measurements() {
        //given
        List<Measurement> sensorList = testData.getTestSensorList();
        //when
        when(sensorListServiceMock.getSensors()).thenReturn(sensorList);
        when(sensorMeasurementMock.collect(anyList())).thenReturn(testData.getTestMeasurements(sensorList));

        //then
        List<MeasurementWeb> measurements = measureNow.collect();

        assertEquals(2, measurements.size());
        verify(sensorListServiceMock,times(1)).getSensors();
        verify(sensorMeasurementMock,times(1)).collect(anyList());
    }
}