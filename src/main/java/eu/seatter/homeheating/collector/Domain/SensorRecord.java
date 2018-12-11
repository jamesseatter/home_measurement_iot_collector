package eu.seatter.homeheating.collector.Domain;

import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 07/12/2018
 * Time: 13:19
 */

public class SensorRecord {
    private Long id;
    private String name;
    private String sensorID;
    private SensorMeasurementType measurementType;
    private SensorType sensorType;
    private LocalDateTime measureTime;
    private Double value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSensorID() {
        return sensorID;
    }

    public void setSensorID(String sensorID) {
        this.sensorID = sensorID;
    }

    public SensorMeasurementType getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(SensorMeasurementType measurementType) {
        this.measurementType = measurementType;
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

    @Override
    public String toString() {
        return "SensorRecord{" +
                "sensorID='" + sensorID + '\'' +
                ", sensorMeasurementSource=" + measurementType +
                ", sensorType=" + sensorType +
                ", measureTime=" + measureTime +
                ", value=" + value +
                '}';
    }
}
