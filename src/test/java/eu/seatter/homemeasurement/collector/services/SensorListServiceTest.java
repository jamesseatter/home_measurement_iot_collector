package eu.seatter.homemeasurement.collector.services;

import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.sensor.listmanagers.SensorListManagerJSON;
import eu.seatter.homemeasurement.collector.sensor.listmanagers.SensorListManagerPi4J;
import eu.seatter.homemeasurement.collector.services.sensor.SensorListServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 11/02/2019
 * Time: 11:36
 */
@ActiveProfiles("test")
class SensorListServiceTest {

    @Mock
    private SensorListManagerJSON sensorListManagerJSON;

    @Mock
    private SensorListManagerPi4J sensorListManagerPi4Jl;

    @InjectMocks
    SensorListServiceImpl sensorListService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void givenSensorFile_whenTwoSensors_thenReturnNoSensors() {
        //given
        List<Measurement> sensorListJSON = new ArrayList<>();
        sensorListJSON.add(Measurement.builder().sensorid("28-000008d2fdb9").familyid(40).build());
        sensorListJSON.add(Measurement.builder().sensorid("28-0000095cd28c").familyid(40).build());

        //when
        when(sensorListManagerJSON.getSensors()).thenReturn(sensorListJSON);

        //then
        List<Measurement> SensorListResults = sensorListService.getSensors();
        assertEquals(0, SensorListResults.size());
        verify(sensorListManagerJSON).getSensors();
    }

    @Test
    void given1WireScan_whenNoSensorFile_thenReturnNoSensors() {
        //given
        List<Measurement> sensorListJSON = new ArrayList<>();
        sensorListJSON.add(Measurement.builder().sensorid("28-000008d2fdb9").familyid(40).build());
        sensorListJSON.add(Measurement.builder().sensorid("28-0000095cd28c").familyid(40).build());

        //when
        when(sensorListManagerPi4Jl.getSensors()).thenReturn(sensorListJSON);

        //then
        List<Measurement> SensorListResults = sensorListService.getSensors();
        assertEquals(0, SensorListResults.size()); // note, sensors are only listed in the json
        verify(sensorListManagerPi4Jl).getSensors();
    }

    @Test
    void givenSensorFileAnd1WireScan_whenTwoSensors_thenReturnSensors() {
        //given
        List<Measurement> sensorListJSON = new ArrayList<>();
        sensorListJSON.add(Measurement.builder().sensorid("28-000008d2fdb9").familyid(40).build());
        sensorListJSON.add(Measurement.builder().sensorid("28-0000095cd28c").familyid(40).build());

        List<Measurement> sensorListPi4j = new ArrayList<>();
        sensorListPi4j.add(Measurement.builder().sensorid("28-000008d2fdb9").familyid(40).build());
        sensorListPi4j.add(Measurement.builder().sensorid("28-0000095cd28c").familyid(40).build());

        //when
        when(sensorListManagerJSON.getSensors()).thenReturn(sensorListJSON);
        when(sensorListManagerPi4Jl.getSensors()).thenReturn(sensorListPi4j);

        //then
        List<Measurement> SensorListResults = sensorListService.getSensors();
        assertEquals(2, SensorListResults.size()); // note, sensors are only listed in the json so only 2 expected
        verify(sensorListManagerJSON).getSensors();
        verify(sensorListManagerPi4Jl).getSensors();
    }
}