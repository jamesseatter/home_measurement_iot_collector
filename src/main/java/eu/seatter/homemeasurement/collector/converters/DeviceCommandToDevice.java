package eu.seatter.homemeasurement.collector.converters;

import eu.seatter.homemeasurement.collector.commands.DeviceCommand;
import eu.seatter.homemeasurement.collector.model.Device;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 18/01/2019
 * Time: 23:40
 */
@Component
public class DeviceCommandToDevice implements Converter<DeviceCommand, Device> {

    @Nullable
    @Override
    public Device convert(DeviceCommand source) {
        if (source == null) {
            return null;
        }

        final Device dest = new Device();

        dest.setName(source.getName());
        dest.setUniqueId(source.getUniqueId());
        dest.setOperatingSystem(source.getOperatingSystem());
        dest.setManufacturer(source.getManufacturer());
        dest.setRegistrationStatus(source.getRegistrationStatus());

        return dest;
    }
}
