package eu.seatter.homemeasurement.collector.services.sensor;

import eu.seatter.homemeasurement.collector.TestData;
import eu.seatter.homemeasurement.collector.model.Sensor;
import eu.seatter.homemeasurement.collector.sensor.listmanagers.SensorListManagerJSON;
import eu.seatter.homemeasurement.collector.sensor.listmanagers.SensorListManagerPi4J;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 02/02/2021
 * Time: 09:29
 */
@ExtendWith(MockitoExtension.class)
class SensorListServiceImplTest {
    private final TestData testData = new TestData();

    @InjectMocks
    private SensorListServiceImpl SensorListServiceImpl;

    @Mock
    private SensorListManagerJSON jsonFile;

    @Mock
    private SensorListManagerPi4J pi4j;

    @BeforeEach
    public void init() {

    }

    @Test
    void whenSensorsDetectedThen2SensorsReturned() {
        //given
        List<Sensor> sensorList;

        //when
        when(jsonFile.getSensors()).thenReturn(testData.getTestSensorList());
        when(pi4j.getSensors()).thenReturn(testData.getTestSensorList());

        //then
        sensorList = SensorListServiceImpl.getSensors();
        assertEquals(2,sensorList.size());
        verify(jsonFile,times(1)).getSensors();
        verify(pi4j,times(1)).getSensors();
    }

    @Test
    void whenNoSensorsDetectedThenNoSensorsReturned() {
        //given
        List<Sensor> sensorList;

        //when
        when(jsonFile.getSensors()).thenReturn(testData.getTestSensorList());
        when(pi4j.getSensors()).thenReturn(Collections.emptyList());

        //then
        sensorList = SensorListServiceImpl.getSensors();
        assertEquals(0,sensorList.size());
        verify(jsonFile,times(1)).getSensors();
        verify(pi4j,times(1)).getSensors();
    }

}