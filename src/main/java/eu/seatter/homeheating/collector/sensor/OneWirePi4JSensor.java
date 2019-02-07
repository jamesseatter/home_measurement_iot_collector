package eu.seatter.homeheating.collector.sensor;

import com.pi4j.component.temperature.TemperatureSensor;
import com.pi4j.component.temperature.impl.TmpDS18B20DeviceType;
import com.pi4j.io.w1.W1Device;
import com.pi4j.io.w1.W1Master;
import eu.seatter.homeheating.collector.exception.SensorNotFoundException;
import eu.seatter.homeheating.collector.model.SensorRecord;
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
    private String sensorDescription;

    private W1Master w1master = new W1Master();

    @Override
    public Double readSensorData(SensorRecord sensorRecord) {
        sensorDescription = "sensor [" + sensorRecord.getSensorID() + "/" + sensorRecord.getSensorType() + "/" + sensorRecord.getFamilyId() + "]";

        if(sensorRecord.getSensorID().isEmpty()) {
            throw new RuntimeException(sensorDescription + " : ID not set");
        }
        sensorID = sensorRecord.getSensorID();

        Optional<W1Device> w1device = getTmpDS18B20();

        if(w1device.isPresent()) {
            Double measurement;
            log.debug(sensorDescription + " : Measurement - " + ((TemperatureSensor) w1device.get()).getTemperature());
            try {
                measurement = ((TemperatureSensor) w1device.get()).getTemperature();
            } catch (Exception e) {
                throw new RuntimeException(sensorDescription + " : Unable to get measurement from sensor");
            }
            return measurement;

        } else {
            throw new SensorNotFoundException("Sensor no longer connected : " + sensorDescription,
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
