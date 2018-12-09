package eu.seatter.homeheating.collector.Domain;

import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 07/12/2018
 * Time: 13:19
 */

public class DataRecord {
    private String SensorID;
    private SensorMeasurementSource sensorMeasurementSource;
    private SensorType sensorType;
    private LocalDateTime measureTime;
    private Double value;

    public String getSensorID() {
        return SensorID;
    }

    public void setSensorID(String sensorID) {
        SensorID = sensorID;
    }

    public SensorMeasurementSource getSensorMeasurementSource() {
        return sensorMeasurementSource;
    }

    public void setSensorMeasurementSource(SensorMeasurementSource sensorMeasurementSource) {
        this.sensorMeasurementSource = sensorMeasurementSource;
    }

    public SensorType getSensorType() {
        return sensorType;
    }

    public void setSensorType(SensorType sensorType) {
        this.sensorType = sensorType;
    }

    public LocalDateTime getMeasureTime() {
        return measureTime;
    }

    public void setMeasureTime(LocalDateTime measureTime) {
        this.measureTime = measureTime;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
