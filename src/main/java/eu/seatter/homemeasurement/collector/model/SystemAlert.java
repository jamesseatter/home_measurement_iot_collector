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
 * Date: 14/02/2020
 * Time: 13:58
 */
@Setter
@Getter
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
public class SystemAlert implements Comparable<SystemAlert> {
    private UUID alertUID;
    private String title;
    //    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime alertTimeUTC; // equals the measurement measurementTimeUTC
    private String message;

    public String loggerFormat() {
        return "[" + title + "]";
    }

    @Override
    public String toString() {
        return "systemAlert{" +
                "title='" + title + '\'' +
                ", message=" + message +
                ", measureTimeUTC=" + alertTimeUTC +
                '}';
    }

    @Override
    public int compareTo(@NotNull SystemAlert that) {
        return this.alertTimeUTC.compareTo(that.alertTimeUTC);
    }
}
