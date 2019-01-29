package eu.seatter.homeheating.collector.Sensor;

import com.pi4j.io.w1.W1Device;
import com.pi4j.io.w1.W1Master;
import eu.seatter.homeheating.collector.Domain.SensorRecord;
import eu.seatter.homeheating.collector.Domain.SensorType;
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
public class SensorListManagerPi4J implements SensorListManager {

    @Override
    public List<SensorRecord> getSensors() {
        List<SensorRecord> sensorRecords = new ArrayList<>();

        W1Master w1Master = new W1Master();
        List<W1Device> w1Devicelist = w1Master.getDevices();

        SensorRecord sensorRecord = new SensorRecord();
        for (W1Device w1d : w1Devicelist) {
            sensorRecord.setSensorID(w1d.getId());
            sensorRecord.setFamilyId(w1d.getFamilyId());
            sensorRecord.setSensorType(SensorType.ONEWIRE);
            sensorRecords.add(sensorRecord);
        }

        return sensorRecords;
    }
}
