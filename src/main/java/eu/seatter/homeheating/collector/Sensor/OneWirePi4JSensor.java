package eu.seatter.homeheating.collector.Sensor;

import com.pi4j.component.temperature.TemperatureSensor;
import com.pi4j.component.temperature.impl.TmpDS18B20DeviceType;
import com.pi4j.io.w1.W1Device;
import com.pi4j.io.w1.W1Master;
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

    private W1Master master = new W1Master();

    private Logger logger = LoggerFactory.getLogger(OneWirePi4JSensor.class);

    public String getSensorID() {
        return sensorID;
    }

    public void setSensorID(String sensorID) {
        this.sensorID = sensorID;
    }

    @Override
    public Double readSensorData() {

        Optional<W1Device> w1device = getTmpDS18B20();

        if(w1device.isPresent()) {
            Double temperature;
            logger.debug("Sensor ID: " + sensorID + " - Temperature : " + ((TemperatureSensor) w1device.get()).getTemperature());
            try {
                temperature = ((TemperatureSensor) w1device.get()).getTemperature();
            } catch (Exception e) {
                throw new RuntimeException("Unable to get temperature from sensor ID : " + sensorID);
            }
            return temperature;
        }
        throw new RuntimeException("No temperature returned for sensor ID : " + sensorID);
    }

    public Optional<W1Device> getTmpDS18B20() {
        Optional<W1Device> w1device = master.getDevices(TmpDS18B20DeviceType.FAMILY_CODE)
                .stream()
                .filter(sensor -> sensor.getId().equals(sensorID))
                .findFirst();
        return w1device;
    }

}
