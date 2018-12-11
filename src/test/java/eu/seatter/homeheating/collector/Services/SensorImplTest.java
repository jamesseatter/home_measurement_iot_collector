package eu.seatter.homeheating.collector.Services;

import eu.seatter.homeheating.collector.Domain.SensorMeasurementType;
import eu.seatter.homeheating.collector.Domain.SensorRecord;
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
    }

    @Test
    void readSensorData() {
        //given
        String sensorId = "9999999999999999";
        Double sensorValue = 20D;

        SensorRecord mockData = new SensorRecord();
        mockData.setSensorID(sensorId);
        mockData.setValue(sensorValue);
        mockData.setSensorType(SensorType.ONEWIRE);
        mockData.setMeasurementType(SensorMeasurementType.TEMPERATURE);

        when(oneWirePi4JSensor.readSensorData(any(SensorRecord.class))).thenReturn(sensorValue);

        //then
        SensorRecord data = sensorService.readSensorData(mockData);

        //when
        assertEquals(sensorValue,data.getValue());
        verify(oneWirePi4JSensor,times(1)).readSensorData(any(SensorRecord.class));
    }
}