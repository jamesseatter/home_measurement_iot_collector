package eu.seatter.homemeasurement.collector.services.sensor;

import eu.seatter.homemeasurement.collector.model.Measurement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 02/02/2021
 * Time: 09:15
 */
class SensorListServiceDevTest {

    SensorListServiceDev sensorListServiceDev = new SensorListServiceDev();
    List<Measurement> sensorList;

    @Test
    void simplegetSensorsNotNull() {
        //given
        sensorList = sensorListServiceDev.getSensors();

        //when

        //then
        assertNotNull(sensorList.size());
        assertEquals(2,sensorList.size());
    }
}