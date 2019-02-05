package eu.seatter.homeheating.collector.services;

import eu.seatter.homeheating.collector.commands.RegistrationCommand;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 31/01/2019
 * Time: 11:19
 */
public interface DeviceService {

    RegistrationCommand registerDevice();
}
