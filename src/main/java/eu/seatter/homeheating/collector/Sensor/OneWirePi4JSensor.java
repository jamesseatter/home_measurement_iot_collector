package eu.seatter.homeheating.collector.Sensor;

import com.pi4j.component.temperature.TemperatureSensor;
import com.pi4j.component.temperature.impl.TmpDS18B20DeviceType;
import com.pi4j.io.w1.W1Device;
import com.pi4j.io.w1.W1Master;
import eu.seatter.homeheating.collector.Domain.SensorRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 07/12/2018
 * Time: 21:13
 */
@Component
public class OneWirePi4JSensor implements Sensor {
    private Double value=0D;
    private String sensorID;
    private String sensorDescription;

    private W1Master w1master = new W1Master();

    private Logger logger = LoggerFactory.getLogger(OneWirePi4JSensor.class);

    @Override
    public Double readSensorData(SensorRecord sensorRecord) {
        sensorDescription = "Sensor [" + sensorRecord.getSensorID() + "/" + sensorRecord.getSensorType() + "/" + sensorRecord.getFamilyId() + "]";

        if(sensorRecord.getSensorID().isEmpty()) {
            throw new RuntimeException(sensorDescription + " : ID not set");
        }
        sensorID = sensorRecord.getSensorID();

        Optional<W1Device> w1device = getTmpDS18B20();

        if(w1device.isPresent()) {
            Double temperature;
            logger.debug(sensorDescription + " : Temperature - " + ((TemperatureSensor) w1device.get()).getTemperature());
            try {
                temperature = ((TemperatureSensor) w1device.get()).getTemperature();
            } catch (Exception e) {
                throw new RuntimeException(sensorDescription + " : Unable to get temperature from sensor");
            }
            return temperature;

        } else {
            throw new RuntimeException(sensorDescription + " : Not found/connected to system");
        }
    }

    private Optional<W1Device> getTmpDS18B20() {
        return w1master.getDevices(TmpDS18B20DeviceType.FAMILY_CODE)
                .stream()
                .filter(sensor -> sensor.getId().equals(sensorID))
                .findFirst();
    }

}
