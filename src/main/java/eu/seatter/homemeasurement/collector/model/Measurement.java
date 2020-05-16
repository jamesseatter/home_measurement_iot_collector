package eu.seatter.homemeasurement.collector.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 07/12/2018
 * Time: 13:19
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder=true)
public class Measurement implements Comparable<Measurement> {
    private UUID recordUID;
    private String sensorid;
    private String title;

    private LocalDateTime measureTimeUTC;
    private Double value;

    private String description;
    private MeasurementUnit measurementUnit;
    private SensorType sensorType;
    private int familyid;

    private Double low_threshold;
    private Double high_threshold;

    private Boolean measurementSentToMq = false;

    private String alertId;
    private String alertgroup;
    private String alertdestination;

    public String loggerFormat() {
        return "[" + sensorid + "/" + sensorType + "/" + familyid + "]";
    }

    @Override
    public String toString() {
        return "measurement{" +
                "sensorid='" + sensorid + '\'' +
                ", sensorType=" + sensorType +
                ", measureTimeUTC=" + measureTimeUTC +
                ", value=" + value + " " + measurementUnit +
                '}';
    }

    @Override
    public int compareTo(Measurement that) {
        return this.measureTimeUTC.compareTo(that.measureTimeUTC);
    }
}
