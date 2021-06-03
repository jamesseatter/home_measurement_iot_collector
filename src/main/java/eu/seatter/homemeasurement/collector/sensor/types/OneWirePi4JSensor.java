package eu.seatter.homemeasurement.collector.sensor.types;

import com.pi4j.component.temperature.TemperatureSensor;
import com.pi4j.component.temperature.impl.TmpDS18B20DeviceType;
import com.pi4j.io.w1.W1Device;
import com.pi4j.io.w1.W1Master;
import eu.seatter.homemeasurement.collector.exception.SensorNotFoundException;
import eu.seatter.homemeasurement.collector.model.MeasurementUnit;
import eu.seatter.homemeasurement.collector.model.Measurement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
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
    private final W1Master w1master = new W1Master();

    @Override
    public Double readSensorData(Measurement measurement) {
        log.info("Processing : " + measurement.loggerFormat());
        if(measurement.getSensorid().isEmpty()) {
            throw new SensorNotFoundException("Sensor is not defined correctlt", measurement.loggerFormat() + " : ID not set");
        }
        String sensorID = measurement.getSensorid();
        Optional<W1Device> w1device;

        if(measurement.getFamilyid() == TmpDS18B20DeviceType.FAMILY_CODE) {
            log.debug("Sensor Family - DS18B20");
            w1device = getTmpDS18B20(sensorID);
            measurement.setMeasurementUnit(MeasurementUnit.C);
        } else {
            log.warn("Sensor Family " + measurement.getFamilyid() + " NOT SUPPORTED");
            return 0.0D;
        }

        if(w1device.isPresent()) {
            double value;

            try {
                value = ((TemperatureSensor) w1device.get()).getTemperature();
                log.debug(measurement.loggerFormat() + " : Measurement - " + value);
            } catch (Exception e) {
                throw new SensorNotFoundException("Sensor not found on the system",measurement.loggerFormat() + " : Unable to get measurement from sensor");
            }
            return value;

        } else {
            throw new SensorNotFoundException("Sensor no longer connected : " + measurement.loggerFormat(),
                    "Verify that sensors are connected to the device and try to restart the service. Verify the logs show the sensors being found.");
        }
    }

    private Optional<W1Device> getTmpDS18B20(String sensorID) {
        log.debug("Looking for ID : '" + sensorID + "'");

        List<W1Device> device = w1master.getDevices(TmpDS18B20DeviceType.FAMILY_CODE);
        log.debug("1Wire device Count : " + device.size());

        for (W1Device w1d : device){
            String devID = w1d.getId().trim();
            log.debug("Validating sensor : '" + devID + "'");
            if(devID.equals(sensorID)) {
                log.debug("Found 1WireDevice name : " + w1d.getName().trim() + " with ID : " + devID);
                return Optional.of(w1d);
            }
        }
        return Optional.empty();
    }
}
