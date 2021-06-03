package eu.seatter.homemeasurement.collector.sensor.listmanagers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.seatter.homemeasurement.collector.model.Measurement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 05/02/2019
 * Time: 11:26
 */
@Component
@Slf4j
public class SensorListManagerJSON implements SensorList {

    @Value("${config.path}")
    private String configPath;

    @Override
    public List<Measurement> getSensors() {
        log.info("Start JSON Sensor list import");
        File sensorFileLocation;

        sensorFileLocation = new File(configPath,"sensorlist.json");
        log.info("Sensor File Location " + sensorFileLocation);

        InputStream inputStream;
        try {
            inputStream = new FileInputStream(sensorFileLocation);
        } catch (FileNotFoundException ex) {
            log.info("Sensor File not found at location");
            return Collections.emptyList();
        }

        log.info("Loading sensors");

        List<Measurement> measurements;
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        TypeReference<List<Measurement>> typeReference = new TypeReference<List<Measurement>>() {};

        try {
            measurements = mapper.readValue(inputStream, typeReference);
        } catch (IOException ex) {
            log.error("Unable to read in JSON file: " + ex.getMessage());
            return Collections.emptyList();
        }
        log.info("Complete JSON Sensor list import finished. Found " + measurements.size() + " sensors");
        return measurements;
    }
}
