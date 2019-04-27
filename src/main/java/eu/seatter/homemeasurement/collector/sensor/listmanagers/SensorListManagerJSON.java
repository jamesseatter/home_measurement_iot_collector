package eu.seatter.homemeasurement.collector.sensor.listmanagers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.seatter.homemeasurement.collector.CollectorApplication;
import eu.seatter.homemeasurement.collector.model.SensorRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
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

    @Override
    public List<SensorRecord> getSensors() {
        log.info("Start JSON Sensor list import");
        File sensorFileLocation;

        try {
            URI path = CollectorApplication.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            sensorFileLocation = new File(new File(path).getParent(), "/config/sensorlist.json");
            log.info("Sensor File Location " + sensorFileLocation.toString());
        } catch (URISyntaxException ex) {
            log.info("Unable to find application location : " + ex.getLocalizedMessage());
            return Collections.emptyList();
        }

        if (!(sensorFileLocation.exists())) {
            log.info("Sensor file does not exist at location. Import terminated");
            return Collections.emptyList();
        }
        log.info("Loading sensors");

        List<SensorRecord> sensorRecords;
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        TypeReference<List<SensorRecord>> typeReference = new TypeReference<List<SensorRecord>>() {};

        InputStream inputStream;
        try {
            inputStream = new FileInputStream(sensorFileLocation);
        } catch (FileNotFoundException ex) {
            log.info("Sensor File not found at location");
            return Collections.emptyList();
        }

        try {
            sensorRecords = mapper.readValue(inputStream, typeReference);

        } catch (IOException ex) {
            log.error("Unable to read in JSON file: " + ex.getMessage());
            return Collections.emptyList();
        }
        log.info("Complete JSON Sensor list import finished. Found " + sensorRecords.size() + " sensors");
        return sensorRecords;

    }
}
