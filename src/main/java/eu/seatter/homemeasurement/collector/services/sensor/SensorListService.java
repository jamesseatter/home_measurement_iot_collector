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
    private SensorListManagerJSON jsonFile;
    private SensorListManagerPi4J pi4j;

    public SensorListService(SensorListManagerJSON jsonFile, SensorListManagerPi4J pi4j) {
        this.jsonFile = jsonFile;
        this.pi4j = pi4j;
    }

    public List<SensorRecord> getSensors() {
        log.info("Getting sensor list");
        List<SensorRecord> sensorRecordList = new ArrayList<>();

        try {
            sensorRecordList.addAll(jsonFile.getSensors());
        } catch (Exception ex) {}

        try {
        sensorRecordList.addAll(pi4j.getSensors());
        } catch (Exception ex) {}

        log.info("Completed sensor list");

        return sensorRecordList;
    }

    private void registerSensors(List<SensorList> sensorList) {

    }
}
