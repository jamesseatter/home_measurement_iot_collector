package eu.seatter.homeheating.collector.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 07/12/2018
 * Time: 13:19
 */
@Getter
@Setter
public class SensorRecord {
    private String sensorID;
    private int familyId;
    private SensorType sensorType;
    private LocalDateTime measureTime;
    private Double value;

    @Override
    public String toString() {
        return "SensorRecord{" +
                "sensorID='" + sensorID + '\'' +
                ", familyID='" + familyId + '\'' +
                ", sensorType=" + sensorType +
                ", measureTime=" + measureTime +
                ", value=" + value +
                '}';
    }
}
