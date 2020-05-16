package eu.seatter.homemeasurement.collector.model;

import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
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

    private LocalDateTime alertTimeUTC;
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
