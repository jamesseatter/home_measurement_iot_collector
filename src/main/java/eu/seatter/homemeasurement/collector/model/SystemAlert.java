package eu.seatter.homemeasurement.collector.model;

import lombok.*;

import java.time.ZonedDateTime;

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
public class SystemAlert {
    private ZonedDateTime time;
    private String alertMessage;
}
