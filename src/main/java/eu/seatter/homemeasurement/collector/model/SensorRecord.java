package eu.seatter.homemeasurement.collector.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;

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
public class SensorRecord {
    private String sensorid;
    private String title;
    private String description;
    private int familyid;
    private SensorType sensorType;
    private SensorMeasurementUnit measurementUnit;
    private Double low_threshold;
    private Double high_threshold;
    private String alertgroup;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime measureTimeUTC;
    private Double value;


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
}
