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
@Builder
public class SensorRecord {
    private String sensorID;
    private int familyId;
    private SensorType sensorType;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime measureTimeUTC;
    private Double value;

    public String loggerFormat() {
        return "[" + sensorID + "/" + sensorType + "/" + getFamilyId() + "]";
    }

    @Override
    public String toString() {
        return "SensorRecord{" +
                "sensorID='" + sensorID + '\'' +
                ", familyID='" + familyId + '\'' +
                ", sensorType=" + sensorType +
                ", measureTimeUTC=" + measureTimeUTC +
                ", value=" + value +
                '}';
    }
}
