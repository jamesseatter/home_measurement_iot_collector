package eu.seatter.homemeasurement.collector.sensor.listmanagers;

import eu.seatter.homemeasurement.collector.model.Sensor;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 30/04/2019
 * Time: 11:32
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SensorListManagerJSON_2Test {

    @TempDir
    static Path sharedTempDir;

    final String sensorBADJSON = "BAD DATA BAD DATA";

    @BeforeAll
    void init() {
        Path tmpFile = sharedTempDir.resolve(sharedTempDir.resolve("sensorList.json"));
        try (FileWriter fileJSON = new FileWriter(String.valueOf(tmpFile))) {
            fileJSON.write(sensorBADJSON);
            fileJSON.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void givenBackJSONData_thenReturnEmptyList() {
        //given
        SensorListManagerJSON sensorListManagerJSON = new SensorListManagerJSON();
        ReflectionTestUtils.setField(sensorListManagerJSON, "configPath", sharedTempDir.toString());

        List<Sensor> sensors = sensorListManagerJSON.getSensors();

        //then
        assertEquals(0,sensors.size());
    }

}