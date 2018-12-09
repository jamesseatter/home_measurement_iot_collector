package eu.seatter.homeheating.collector.Sensor;

import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 07/12/2018
 * Time: 21:13
 */
@Component
public class SensorOneWirePi4J implements SensorReader {
    private Double value=20D;
    private String pinNumber;
    private String pinName;
    private String pinResistance;

    @Override
    public Double readSensorData(String sensorID) {
        return value;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getPinNumber() {
        return pinNumber;
    }

    public void setPinNumber(String pinNumber) {
        this.pinNumber = pinNumber;
    }

    public String getPinName() {
        return pinName;
    }

    public void setPinName(String pinName) {
        this.pinName = pinName;
    }

    public String getPinResistance() {
        return pinResistance;
    }

    public void setPinResistance(String pinResistance) {
        this.pinResistance = pinResistance;
    }
}
