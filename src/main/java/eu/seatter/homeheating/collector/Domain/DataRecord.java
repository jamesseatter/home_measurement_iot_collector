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
    private SensorType sensorType;
    private LocalDateTime measureTime;
    private int Data;

    public String getSensorID() {
        return SensorID;
    }

    public void setSensorID(String sensorID) {
        SensorID = sensorID;
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

    public int getData() {
        return Data;
    }

    public void setData(int data) {
        Data = data;
    }
}
