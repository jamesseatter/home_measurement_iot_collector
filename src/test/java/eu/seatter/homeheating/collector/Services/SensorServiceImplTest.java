package eu.seatter.homeheating.collector.Services;

import eu.seatter.homeheating.collector.Domain.DataRecord;
import eu.seatter.homeheating.collector.Domain.SensorType;
import eu.seatter.homeheating.collector.Sensor.SensorOneWirePi4J;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 09/12/2018
 * Time: 15:25
 */
class SensorServiceImplTest {

    private SensorService sensorService;

    @Mock
    private SensorOneWirePi4J sensorOneWirePi4J;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        sensorService = new SensorServiceImpl(sensorOneWirePi4J);
    }

    @Test
    void readSensorData() {
        //given
        SensorType sensorType = SensorType.ONEWIRE;
        String sensorId = "GPIO_1";
        Double sensorValue = 20D;

        DataRecord mockData = new DataRecord();
        mockData.setSensorID(sensorId);
        mockData.setValue(sensorValue);

        when(sensorOneWirePi4J.readSensorData(anyString())).thenReturn(sensorValue);

        //then
        DataRecord data = sensorService.readSensorData(mockData);

        //when
        assertEquals(sensorValue,data.getValue());
        verify(sensorOneWirePi4J,times(1)).readSensorData(anyString());
    }
}