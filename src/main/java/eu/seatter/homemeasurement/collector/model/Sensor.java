package eu.seatter.homemeasurement.collector.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import eu.seatter.homemeasurement.collector.model.enums.MeasurementUnit;
import eu.seatter.homemeasurement.collector.model.enums.SensorType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;
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
public class Sensor implements Comparable<Sensor> {
    private UUID recordUID;
    private String sensorid;
    private String title;
    private String shortTitle;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime measureTimeUTC;
    private Double value;

    private String description;
    private MeasurementUnit measurementUnit;

    @Builder.Default
    private Double measurementadjustmentvalue = 0.0d;
    private SensorType sensortype;
    private int familyid;

    private Double low_threshold;
    private Double high_threshold;

    private Boolean measurementSentToMq = false;
    private Boolean measurementSentToAzure = false;

    private String alertId;
    private String alertgroup;
    private String alertdestination;

    public String loggerFormat() {
        return "[" + sensorid + "/" + sensortype + "/" + familyid + "]";
    }

    @Override
    public String toString() {
        return "measurement{" +
                "sensorid='" + sensorid + '\'' +
                ", sensorType=" + sensortype +
                ", measureTimeUTC=" + measureTimeUTC +
                ", value=" + value + " " + measurementUnit +
                '}';
    }

    @Override
    public int compareTo(Sensor that) {
        return this.measureTimeUTC.compareTo(that.measureTimeUTC);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recordUID, measureTimeUTC, sensorid);
    }

}
