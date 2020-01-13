package eu.seatter.homemeasurement.collector.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import lombok.*;

import java.time.ZonedDateTime;

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
public class SensorRecord implements Comparable<SensorRecord> {
    private String sensorid;
    private String title;
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime measureTimeUTC;
    private Double value;

    private String description;
    private SensorMeasurementUnit measurementUnit;
    private SensorType sensorType;
    private int familyid;

    private Double low_threshold;
    private Double high_threshold;
    private String alertgroup;
    private String alertdestination;

    public String loggerFormat() {
        return "[" + sensorid + "/" + sensorType + "/" + familyid + "]";
    }

    @Override
    public String toString() {
        return "SensorRecord{" +
                "sensorid='" + sensorid + '\'' +
                ", sensorType=" + sensorType +
                ", measureTimeUTC=" + measureTimeUTC +
                ", value=" + value + " " + measurementUnit +
                '}';
    }

    @Override
    public int compareTo(SensorRecord that) {
        return this.measureTimeUTC.compareTo(that.measureTimeUTC);
    }
}
