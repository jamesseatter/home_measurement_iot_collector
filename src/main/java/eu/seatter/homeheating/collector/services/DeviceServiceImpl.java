package eu.seatter.homeheating.collector.services;

import eu.seatter.homeheating.collector.commands.RegistrationCommand;
import eu.seatter.homeheating.collector.converters.DeviceCommandToDevice;
import eu.seatter.homeheating.collector.converters.DeviceToDeviceCommand;
import eu.seatter.homeheating.collector.model.Device;
import eu.seatter.homeheating.collector.model.DeviceIdentification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 31/01/2019
 * Time: 11:20
 */
@Slf4j
@Service
public class DeviceServiceImpl implements DeviceService {

    private final RESTClientService restClientService;
    private final DeviceToDeviceCommand converterDeviceToDeviceCommand;
    private final DeviceCommandToDevice converterDeviceCommandToDevice;
    private final EncryptionService encryptionService;
    private final DeviceIdentification di;

    public DeviceServiceImpl(RESTClientService restClientService, DeviceToDeviceCommand converterDeviceToDeviceCommand, DeviceCommandToDevice converterDeviceCommandToDevice, EncryptionService encryptionService) {
        this.restClientService = restClientService;
        this.converterDeviceToDeviceCommand = converterDeviceToDeviceCommand;
        this.converterDeviceCommandToDevice = converterDeviceCommandToDevice;
        this.encryptionService = encryptionService;
        this.di = DeviceIdentification.INSTANCE.getInstance();
    }

    @Override
    public RegistrationCommand registerDevice() {
        Device device = new Device();
        device.setName(di.getHostName());
        device.setOperatingSystem(di.getOs());
        device.setManufacturer(di.getManufacturer());
        device.setUniqueId(encryptionService.encrypteString(di.getMacAddress()));

        log.info("Register device with edge");
        RegistrationCommand deviceRegistration = new RegistrationCommand();

        try {
            //deviceRegistration = restClientService.isCollectorRegistered(device.getUniqueId());
        } catch (Exception ex) {
            log.error("ERROR in RestService: " + ex.getLocalizedMessage());
            throw new RuntimeException(ex.getLocalizedMessage());
        }

        if(deviceRegistration.getUniqueId() != null && (deviceRegistration.getUniqueId().equals(device.getUniqueId()))) {
            log.info("Device was previously registered. Current registration status : " + deviceRegistration.getRegistrationStatus());
            device.setRegistrationStatus(deviceRegistration.getRegistrationStatus());
        } else {
            deviceRegistration = restClientService.registerCollector(device);
            log.info("Device has been registered. Current registration status : " + deviceRegistration.getRegistrationStatus());
        }

        return deviceRegistration;
    }
}
