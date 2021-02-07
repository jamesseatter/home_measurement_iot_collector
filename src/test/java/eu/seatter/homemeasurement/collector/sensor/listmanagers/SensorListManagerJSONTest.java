package eu.seatter.homemeasurement.collector.sensor.listmanagers;

import eu.seatter.homemeasurement.collector.model.Measurement;
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
public class SensorListManagerJSONTest {

    @TempDir
    static Path sharedTempDir;

    String sensorJSON = "[\n" +
            "  {\n" +
            "    \"sensorid\": \"28-000008d2fdb9\",\n" +
            "    \"title\" : \"Température de l'eau à l'arrivée\",\n" +
            "    \"description\" : \"Returns the temperature of the hot water entering the house from the central heating system.\",\n" +
            "    \"familyid\": \"40\",\n" +
            "    \"sensortype\": \"ONEWIRE\",\n" +
            "    \"measurementUnit\": \"C\",\n" +
            "    \"measurementadjustmentvalue\" : \"17\",\n" +
            "    \"low_threshold\": 45,\n" +
            "    \"high_threshold\": 60,\n" +
            "    \"alertgroup\": \"temperature_threshold_alerts_private\",\n" +
            "    \"alertdestination\": \"BORRY\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"sensorid\": \"28-0000095fcafb\",\n" +
            "    \"title\" : \"Température de l'eau de chaudière\",\n" +
            "    \"description\" : \"Returns the temperature of the hot water in the boiler.\",\n" +
            "    \"familyid\": \"40\",\n" +
            "    \"sensortype\": \"ONEWIRE\",\n" +
            "    \"measurementUnit\": \"C\",\n" +
            "    \"low_threshold\": 40,\n" +
            "    \"high_threshold\": 60,\n" +
            "    \"alertgroup\": \"temperature_threshold_alerts_private\",\n" +
            "    \"alertdestination\": \"PRIVATE\"\n" +
            "  }\n" +
            "]";

    String sensorBADJSON = "BAD DATA BAD DATA";

    @BeforeAll
    void init() {
        Path tmpFile = sharedTempDir.resolve(sharedTempDir.resolve("sensorList.json"));
        try (FileWriter fileJSON = new FileWriter(String.valueOf(tmpFile))) {
            fileJSON.write(sensorJSON);
            fileJSON.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(1)
    void givenTestSensorFile2Sensors_then2SensorsReadIn() {
        //given
        SensorListManagerJSON sensorListManagerJSON = new SensorListManagerJSON();
        ReflectionTestUtils.setField(sensorListManagerJSON, "configPath", sharedTempDir.toString());
        List<Measurement> sensors = sensorListManagerJSON.getSensors();

        //then
        assertEquals(2,sensors.size());
    }

    @Test
    @Order(2)
    void givenBadFilePath_thenReturnEmptyList() {
        SensorListManagerJSON sensorListManagerJSON = new SensorListManagerJSON();
        ReflectionTestUtils.setField(sensorListManagerJSON, "configPath", "BADFILEPATH");

        List<Measurement> sensors = sensorListManagerJSON.getSensors();

        //then
        assertEquals(0,sensors.size());
    }

    //Tests after 10 will use the sensorBADJSON data so be carefull

    @Test
    @Order(10)
    void givenBackJSONData_thenReturnEmptyList() {
        //given
        Path tmpFile = sharedTempDir.resolve(sharedTempDir.resolve("sensorList.json"));
        try (FileWriter fileJSON = new FileWriter(String.valueOf(tmpFile))) {
            fileJSON.write(sensorBADJSON);
            fileJSON.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SensorListManagerJSON sensorListManagerJSON = new SensorListManagerJSON();
        ReflectionTestUtils.setField(sensorListManagerJSON, "configPath", sharedTempDir.toString());

        List<Measurement> sensors = sensorListManagerJSON.getSensors();

        //then
        assertEquals(0,sensors.size());
    }

}