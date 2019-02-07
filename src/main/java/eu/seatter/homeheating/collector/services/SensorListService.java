package eu.seatter.homeheating.collector.services;

import eu.seatter.homeheating.collector.model.SensorRecord;
import eu.seatter.homeheating.collector.sensor.SensorListManagerJSON;
import eu.seatter.homeheating.collector.sensor.SensorListManagerPi4J;
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

        sensorRecordList.addAll(jsonFile.getSensors());
        sensorRecordList.addAll(pi4j.getSensors());
        log.info("Completed sensor list");
        return sensorRecordList;
    }
}
