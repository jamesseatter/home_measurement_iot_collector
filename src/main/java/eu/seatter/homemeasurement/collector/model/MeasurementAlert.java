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
    private UUID alertUID;
    private String title;
    //    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime alertTimeUTC; // equals the measurement measurementTimeUTC

    private Double value;
    private MeasurementUnit measurementUnit;

    private String message;

    private boolean alertSentEmail;
    private String alertSentEmailTO;
    private boolean alertSentMQ;

    private String environment;

    public String loggerFormat() {
        return "[" + title + "]";
    }

    @Override
    public String toString() {
        return "measurement{" +
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
