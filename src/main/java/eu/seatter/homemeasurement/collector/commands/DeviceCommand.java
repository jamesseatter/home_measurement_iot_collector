package eu.seatter.homemeasurement.collector.commands;

import eu.seatter.homemeasurement.collector.model.RegistrationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 01/01/2019
 * Time: 13:42
 */
@Setter
@Getter
@NoArgsConstructor
@Component
public class DeviceCommand {
    @NotNull
    private String name;
    @NotNull
    private String uniqueId;
    @NotNull
    private String manufacturer;
    @NotNull
    private String operatingSystem;
    private RegistrationStatus registrationStatus;
    private String registrationCode;

}
