package eu.seatter.homeheating.collector.commands;

import eu.seatter.homeheating.collector.model.RegistrationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 27/01/2019
 * Time: 22:50
 */
@Setter
@Getter
@NoArgsConstructor
@Component
public class RegistrationCommand {

    private Long id;
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
