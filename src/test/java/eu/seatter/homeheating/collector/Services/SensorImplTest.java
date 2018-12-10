package eu.seatter.homeheating.collector.Services;

import eu.seatter.homeheating.collector.Domain.DataRecord;
import eu.seatter.homeheating.collector.Domain.SensorMeasurementSource;
import eu.seatter.homeheating.collector.Domain.SensorType;
import eu.seatter.homeheating.collector.Sensor.OneWirePi4JSensor;
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
class SensorImplTest {

    private SensorService sensorService;

    @Mock
    private OneWirePi4JSensor oneWirePi4JSensor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        sensorService = new SensorServiceImpl(oneWirePi4JSensor);
        oneWirePi4JSensor.setSensorID("0000000000000000");
    }

    @Test
    void readSensorData() {
        //given
        SensorType sensorType = SensorType.ONEWIRE;
        String sensorId = "9999999999999999";
        Double sensorValue = 20D;

        DataRecord mockData = new DataRecord();
        mockData.setSensorID(sensorId);
        mockData.setValue(sensorValue);
        mockData.setSensorType(SensorType.ONEWIRE);
        mockData.setSensorMeasurementSource(SensorMeasurementSource.TEMPERATURE);

        when(oneWirePi4JSensor.readSensorData()).thenReturn(sensorValue);

        //then
        DataRecord data = sensorService.readSensorData(mockData);

        //when
        assertEquals(sensorValue,data.getValue());
        verify(oneWirePi4JSensor,times(1)).readSensorData();
    }
}