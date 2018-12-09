package eu.seatter.homeheating.collector.Services;

import eu.seatter.homeheating.collector.Domain.DataRecord;
import eu.seatter.homeheating.collector.Sensor.SensorReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 08/12/2018
 * Time: 15:15
 */
@Service
public class SensorServiceImpl implements SensorService {

    private Logger logger = LoggerFactory.getLogger(SensorServiceImpl.class);

    private SensorReader sensorReader;

    public SensorServiceImpl(SensorReader sensorReader) {
        this.sensorReader = sensorReader;
    }

    @Override
    public DataRecord readSensorData(DataRecord sensorRecord) {

        try {
            sensorRecord.setValue(sensorReader.readSensorData(sensorRecord.getSensorID()));
        }
        catch (Exception e) {
            logger.error("Caught error : " + e.getMessage());
        }

        return sensorRecord;
    }
}
