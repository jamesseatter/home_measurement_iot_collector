package eu.seatter.homemeasurement.collector.services.device;

import eu.seatter.homemeasurement.collector.commands.RegistrationCommand;
import eu.seatter.homemeasurement.collector.model.Device;
import eu.seatter.homemeasurement.collector.model.DeviceIdentification;
import eu.seatter.homemeasurement.collector.services.RegistrationService;
import eu.seatter.homemeasurement.collector.services.encryption.EncryptionService;
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

    private final RegistrationService restClientService;
    private final EncryptionService encryptionService;
    private final DeviceIdentification di;

    public DeviceServiceImpl(RegistrationService restClientService, EncryptionService encryptionService) {
        this.restClientService = restClientService;
        this.encryptionService = encryptionService;
        this.di = DeviceIdentification.INSTANCE.getInstance();
    }

    @Override
    public RegistrationCommand registerDevice() {
        Device device = new Device();
        device.setName(di.getHostName());
        device.setOperatingSystem(di.getOs());
        device.setManufacturer(di.getManufacturer());
        device.setUniqueId(encryptionService.encryptString(di.getMacAddress()));

        log.info("Register device with edge");
        RegistrationCommand deviceRegistration;

        try {
            deviceRegistration = restClientService.isCollectorRegistered(device.getUniqueId());
        } catch (Exception ex) {
            log.error("ERROR in RestService: " + ex.getLocalizedMessage());
            throw new RuntimeException(ex.getLocalizedMessage());
        }

        if(deviceRegistration.getUniqueId() != null && (deviceRegistration.getUniqueId().equals(device.getUniqueId()))) {
            log.info("device was previously registered. Current registration status : " + deviceRegistration.getRegistrationStatus());
            device.setRegistrationStatus(deviceRegistration.getRegistrationStatus());
        } else {
            deviceRegistration = restClientService.registerCollector(device);
            log.info("device has been registered. Current registration status : " + deviceRegistration.getRegistrationStatus());
        }

        return deviceRegistration;
    }
}
