package eu.seatter.homemeasurement.collector.services.messaging;

import eu.seatter.homemeasurement.collector.model.Device;
import eu.seatter.homemeasurement.collector.model.SensorRecord;
import eu.seatter.homemeasurement.collector.sensor.types.Sensor;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 26/03/2019
 * Time: 13:43
 */
@Service
public class RESTService implements Messaging {
    @Override
    public void sendMeasurement(SensorRecord sensorRecord) {

    }

    @Override
    public void registerDevice(Device device) {

    }

    @Override
    public void registerSensor(Sensor sensor) {

    }
}
