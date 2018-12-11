package eu.seatter.homeheating.collector.Services;

import eu.seatter.homeheating.collector.Domain.SensorRecord;
import eu.seatter.homeheating.collector.Sensor.Sensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 08/12/2018
 * Time: 15:15
 */
@Service
public class SensorServiceImpl implements eu.seatter.homeheating.collector.Services.SensorService {

    private Logger logger = LoggerFactory.getLogger(SensorServiceImpl.class);

    private Sensor sensor;

    public SensorServiceImpl(Sensor sensor) {
        this.sensor = sensor;
    }

    @Override
    public SensorRecord readSensorData(SensorRecord sensorRecord) {

        try {
            sensorRecord.setValue(sensor.readSensorData(sensorRecord));
            sensorRecord.setMeasureTime(LocalDateTime.now(ZoneOffset.UTC));
        }
        catch (RuntimeException e) {
            logger.error(e.getMessage());
            throw e;
        }

        return sensorRecord;
    }
}
