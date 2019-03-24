package eu.seatter.homemeasurement.collector.model;

import lombok.*;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 31/01/2019
 * Time: 09:36
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class Device implements Comparable<Device> {
    @NotNull
    @Size(max = 30)
    private String name;

    @NotNull
    @Size(max = 50)
    private String uniqueId;

    @NotNull
    @Size(max = 50)
    private String manufacturer;

    @NotNull
    @Size(max = 50)
    private String operatingSystem;

    private RegistrationStatus registrationStatus = RegistrationStatus.NOTREGISTERED;

    private String registrationCode;

    @Override
    public int compareTo(Device o) {
        return getUniqueId().compareTo(o.getUniqueId());
    }
}
