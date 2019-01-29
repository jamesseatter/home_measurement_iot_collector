package eu.seatter.homeheating.collector.services;

import eu.seatter.homeheating.collector.domain.SensorRecord;
import eu.seatter.homeheating.collector.domain.SensorType;
import eu.seatter.homeheating.collector.sensor.SensorListManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 09/12/2018
 * Time: 15:25
 */
@ExtendWith(MockitoExtension.class)
class IOTServiceImplTest {

    @Autowired
    private IOTService sensorService;

    @MockBean
    private SensorListManager sensorListManager;

    @Mock
    private List<SensorRecord> sensorList = new ArrayList<>();

    private SensorRecord mockData;


    @BeforeEach
    void setUp() {

    }


    @Test
    void readSensorValue() {
        //given
        String sensorId = "9999999999999999";
        Double sensorValue = 20D;

        SensorRecord mockData = new SensorRecord();
        mockData.setSensorID(sensorId);
        mockData.setValue(sensorValue);
        mockData.setSensorType(SensorType.ONEWIRE);

        sensorList.add(mockData);

        //when(sensorService.readSensorValue(any(SensorRecord.class))).thenReturn(mockData);

        //then
        //SensorRecord data = sensorService.readSensorValue(mockData);

        //when
        //assertEquals(sensorValue,data.getValue());
        //verify(sensorService,times(1)).readSensorValue(any(SensorRecord.class));
    }
}