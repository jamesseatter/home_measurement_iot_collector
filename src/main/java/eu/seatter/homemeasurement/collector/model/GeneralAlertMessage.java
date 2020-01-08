package eu.seatter.homemeasurement.collector.model;

import lombok.*;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 06/06/2019
 * Time: 13:02
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder=true)
public class GeneralAlertMessage {
    private String message;
    private String alertgroup;
}
