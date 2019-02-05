package eu.seatter.homeheating.collector.sensor;

import com.pi4j.io.w1.W1Device;
import com.pi4j.io.w1.W1Master;
import eu.seatter.homeheating.collector.model.SensorRecord;
import eu.seatter.homeheating.collector.model.SensorType;
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
public class SensorListManagerPi4J implements SensorListManager {

    @Override
    public List<SensorRecord> getSensors() {
        log.info("1-Wire sensor scan started");
        List<SensorRecord> sensorRecords = new ArrayList<>();

        W1Master w1Master = new W1Master();
        List<W1Device> w1Devicelist = w1Master.getDevices();

        for (W1Device w1d : w1Devicelist) {
            SensorRecord sensor = new SensorRecord();
            sensor.setSensorID(w1d.getId());
            sensor.setFamilyId(w1d.getFamilyId());
            sensor.setSensorType(SensorType.ONEWIRE);
            sensorRecords.add(sensor);
            log.info("  Found sensor : " + sensor.toString());
        }
        log.info("1-Wire sensor scan finished. Found " + sensorRecords.size() + " sensors");
        return sensorRecords;
    }
}
