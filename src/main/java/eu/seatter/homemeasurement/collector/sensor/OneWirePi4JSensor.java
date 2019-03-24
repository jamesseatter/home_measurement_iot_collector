package eu.seatter.homemeasurement.collector.sensor;

import com.pi4j.component.temperature.TemperatureSensor;
import com.pi4j.component.temperature.impl.TmpDS18B20DeviceType;
import com.pi4j.io.w1.W1Device;
import com.pi4j.io.w1.W1Master;
import eu.seatter.homemeasurement.collector.exception.SensorNotFoundException;
import eu.seatter.homemeasurement.collector.model.SensorRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 07/12/2018
 * Time: 21:13
 */
@Slf4j
@Component
public class OneWirePi4JSensor implements Sensor {
    private Double value=0D;
    private String sensorID;

    private W1Master w1master = new W1Master();

    @Override
    public Double readSensorData(SensorRecord sensorRecord) {
        log.info("Processing : " + sensorRecord.loggerFormat());
        if(sensorRecord.getSensorID().isEmpty()) {
            throw new RuntimeException(sensorRecord.loggerFormat() + " : ID not set");
        }
        sensorID = sensorRecord.getSensorID();

        Optional<W1Device> w1device = getTmpDS18B20();

        if(w1device.isPresent()) {
            Double measurement;
            log.debug(sensorRecord.loggerFormat() + " : Measurement - " + ((TemperatureSensor) w1device.get()).getTemperature());
            try {
                measurement = ((TemperatureSensor) w1device.get()).getTemperature();
            } catch (Exception e) {
                throw new RuntimeException(sensorRecord.loggerFormat() + " : Unable to get measurement from sensor");
            }
            return measurement;

        } else {
            throw new SensorNotFoundException("Sensor no longer connected : " + sensorRecord.loggerFormat(),
                    "Verify that sensors are connected to the device and try to restart the service. Verify the logs show the sensors being found.");
        }
    }

    private Optional<W1Device> getTmpDS18B20() {
        return w1master.getDevices(TmpDS18B20DeviceType.FAMILY_CODE)
                .stream()
                .filter(sensor -> sensor.getId().equals(sensorID))
                .findFirst();
    }

}
