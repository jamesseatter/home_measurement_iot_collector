package eu.seatter.homemeasurement.collector.services.sensor;

import eu.seatter.homemeasurement.collector.model.SensorRecord;
import eu.seatter.homemeasurement.collector.sensor.listmanagers.SensorList;
import eu.seatter.homemeasurement.collector.sensor.listmanagers.SensorListManagerJSON;
import eu.seatter.homemeasurement.collector.sensor.listmanagers.SensorListManagerPi4J;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 05/02/2019
 * Time: 11:35
 */
@Slf4j
@Service
public class SensorListService {
    private final SensorList jsonFile;
    private final SensorList pi4j;

    public SensorListService(SensorListManagerJSON jsonFile, SensorListManagerPi4J pi4j) {
        this.jsonFile = jsonFile;
        this.pi4j = pi4j;
    }

    public List<SensorRecord> getSensors() {
        log.info("Getting sensor list");
        List<SensorRecord> sensorRecordList = new ArrayList<>();

        //The json file defines a list of non-discoverable sensors
        //and configuration information for discoverable sensors.
        try {
            sensorRecordList.addAll(jsonFile.getSensors());
        } catch (Exception ex) {
            log.error(ex.getLocalizedMessage());
        }

        try {
            @SuppressWarnings("unchecked")
            List<SensorRecord>  pi4jSensors = new ArrayList<>(pi4j.getSensors());
            //for each 1-wire sensor found validate that it is in the configuration file and can be measured.
            boolean sensorFound = false;
            for (SensorRecord pi4jSensor : pi4jSensors) {
                for (int srIndex = 0; srIndex < sensorRecordList.size(); srIndex++) {
                    if (sensorRecordList.get(srIndex).getSensorid().trim().equals(pi4jSensor.getSensorid().trim())) {
                        sensorFound = true;
                        log.info("Sensor " + pi4jSensors.get(srIndex).getSensorid().trim() + " found in main configuration");
                    }
                }
                if (!sensorFound) {
                    log.warn("Sensor " + pi4jSensor.getSensorid().trim() + " NOT found in main configuration and will not be measured. To measure the sensor value add it to the main configuration.");
                }
            }
        } catch (Exception ex) {}

        log.info("Completed sensor list");

        return sensorRecordList;
    }
}
