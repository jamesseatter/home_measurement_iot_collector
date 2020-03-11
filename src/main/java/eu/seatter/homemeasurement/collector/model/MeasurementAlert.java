package eu.seatter.homemeasurement.collector.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 11/03/2020
 * Time: 18:30
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder=true)
public class MeasurementAlert  implements Comparable<MeasurementAlert> {
        private UUID alertUUID;
    private String title;
    //    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime alertTimeUTC; // equals the SensorRecord measurementTimeUTC

    private Double value;
    private SensorMeasurementUnit measurementUnit;

    private String message;

    private boolean alertSentEmail;
    private String alertSentEmailTO;

    public String loggerFormat() {
        return "[" + title + "]";
    }

    @Override
    public String toString() {
        return "SensorRecord{" +
                "title='" + title + '\'' +
                ", value=" + value + " " + measurementUnit.toString() +
                ", measureTimeUTC=" + alertTimeUTC +
                '}';
    }

    @Override
    public int compareTo(@NotNull MeasurementAlert that) {
        return this.alertTimeUTC.compareTo(that.alertTimeUTC);
    }
}
