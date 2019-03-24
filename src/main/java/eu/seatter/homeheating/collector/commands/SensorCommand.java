package eu.seatter.homeheating.collector.commands;

import eu.seatter.homeheating.collector.model.SensorType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 07/02/2019
 * Time: 22:56
 */
@Setter
@Getter
@NoArgsConstructor
@Component
public class SensorCommand {
    @NotNull
    private String sensorID;
    @NotNull
    private int familyId;
    @NotNull
    private SensorType sensorType;
    @NotNull
    private LocalDateTime measureTime;
    @NotNull
    private Double value;
}
