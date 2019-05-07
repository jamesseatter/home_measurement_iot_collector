package eu.seatter.homemeasurement.collector.services;

import eu.seatter.homemeasurement.collector.model.SensorRecord;
import eu.seatter.homemeasurement.collector.sensor.listmanagers.SensorListManagerJSON;
import eu.seatter.homemeasurement.collector.sensor.listmanagers.SensorListManagerPi4J;
import eu.seatter.homemeasurement.collector.services.sensor.SensorListService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 11/02/2019
 * Time: 11:36
 */
@RunWith(MockitoJUnitRunner.class)
public class SensorListServiceTest {

    @Mock
    private SensorListManagerJSON sensorListManagerJSON;

    @Mock
    private SensorListManagerPi4J sensorListManagerPi4Jl;

    @InjectMocks
    SensorListService sensorListService;


    @Test
    public void givenSensorFile_whenTwoSensors_thenReturnSensors() {
        //given
        List<SensorRecord> sensorList = new ArrayList<>();
        sensorList.add(SensorRecord.builder().sensorid("20-12345").familyid(40).build());
        sensorList.add(SensorRecord.builder().sensorid("20-54321").familyid(40).build());

        //when
        when(sensorListManagerJSON.getSensors()).thenReturn(sensorList);

        //then
        List<SensorRecord> SensorListResults = sensorListService.getSensors();
        assertEquals(SensorListResults.size(),2);
        verify(sensorListManagerJSON).getSensors();
    }

    @Test
    public void given1WireScan_whenNoSensorFile_thenReturnNoSensors() {
        //given
        List<SensorRecord> sensorList = new ArrayList<>();
        sensorList.add(SensorRecord.builder().sensorid("20-12345").familyid(40).build());
        sensorList.add(SensorRecord.builder().sensorid("20-54321").familyid(40).build());

        //when
        when(sensorListManagerPi4Jl.getSensors()).thenReturn(sensorList);

        //then
        List<SensorRecord> SensorListResults = sensorListService.getSensors();
        assertEquals(SensorListResults.size(),0); // note, sensors are only listed in the json
        verify(sensorListManagerPi4Jl).getSensors();
    }

    @Test
    public void givenSensorFileAnd1WireScan_whenTwoSensors_thenReturnSensors() {
        //given
        List<SensorRecord> sensorList = new ArrayList<>();
        sensorList.add(SensorRecord.builder().sensorid("20-12345").familyid(40).build());
        sensorList.add(SensorRecord.builder().sensorid("20-54321").familyid(40).build());

        List<SensorRecord> sensorListPi4j = new ArrayList<>();
        sensorListPi4j.add(SensorRecord.builder().sensorid("20-98763").familyid(30).build());
        sensorListPi4j.add(SensorRecord.builder().sensorid("20-97654").familyid(30).build());

        //when
        when(sensorListManagerJSON.getSensors()).thenReturn(sensorList);
        when(sensorListManagerPi4Jl.getSensors()).thenReturn(sensorListPi4j);

        //then
        List<SensorRecord> SensorListResults = sensorListService.getSensors();
        assertEquals(SensorListResults.size(),2); // note, sensors are only listed in the json so only 2 expected
        verify(sensorListManagerJSON).getSensors();
        verify(sensorListManagerPi4Jl).getSensors();
    }
}