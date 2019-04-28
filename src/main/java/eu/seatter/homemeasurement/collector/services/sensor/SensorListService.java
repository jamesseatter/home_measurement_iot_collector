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

        //The josn file defines a list of non-discoverable sensors
        //and configuration information for discoverable sensors.
        try {
            sensorRecordList.addAll(jsonFile.getSensors());
        } catch (Exception ex) {}

        try {
            List<SensorRecord> pi4jSensors = new ArrayList<>(pi4j.getSensors());
            //for each sensor found, update the main sensorRecordList by either adding the sensor or updating an existing sensor entry




        } catch (Exception ex) {}

        log.info("Completed sensor list");

        return sensorRecordList;
    }

//    private void registerSensors(List<SensorList> sensorList) {
//
//    }
}
