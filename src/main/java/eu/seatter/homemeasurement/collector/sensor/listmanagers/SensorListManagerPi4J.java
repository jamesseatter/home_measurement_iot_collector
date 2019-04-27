package eu.seatter.homemeasurement.collector.sensor.listmanagers;

import com.pi4j.io.w1.W1Device;
import com.pi4j.io.w1.W1Master;
import eu.seatter.homemeasurement.collector.model.SensorRecord;
import eu.seatter.homemeasurement.collector.model.SensorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 29/01/2019
 * Time: 12:36
 */
@Component
@Slf4j
public class SensorListManagerPi4J implements SensorList {

    @Override
    public List<SensorRecord> getSensors() {
        log.info("Start 1-Wire sensor scan");
        List<SensorRecord> sensorRecords = new ArrayList<>();

        W1Master w1Master = new W1Master();
        List<W1Device> w1DeviceList = w1Master.getDevices();
        log.info("Found : " + w1DeviceList.size());
        for (W1Device w1d : w1DeviceList) {
            log.debug("1 wire device : " + w1d.getName());
            SensorRecord sensor = new SensorRecord();
            sensor.setSensorid(w1d.getId().trim());
            sensor.setFamilyid(w1d.getFamilyId());
            sensor.setSensorType(SensorType.ONEWIRE);
            sensorRecords.add(sensor);
            log.info("  Found sensor : " + sensor.loggerFormat());
        }
        log.info("Completed 1-Wire sensor scan finished. Found " + sensorRecords.size() + " sensors");
        return sensorRecords;
    }
}
