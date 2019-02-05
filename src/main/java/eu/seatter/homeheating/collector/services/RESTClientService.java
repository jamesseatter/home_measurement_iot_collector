package eu.seatter.homeheating.collector.services;

import eu.seatter.homeheating.collector.commands.DeviceCommand;
import eu.seatter.homeheating.collector.commands.RegistrationCommand;
import eu.seatter.homeheating.collector.converters.DeviceToDeviceCommand;
import eu.seatter.homeheating.collector.model.Device;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 29/01/2019
 * Time: 23:14
 */
@Service
@Slf4j
public class RESTClientService {

    @Value("${app.edge.uri}")
    private String edgeURI;

    private String baseRegistrationURI = "api/v1/registration";

    private DeviceCommand deviceCommand;
    private DeviceToDeviceCommand converterDeviceToDeviceCommand;

    public RESTClientService(DeviceCommand deviceCommand, DeviceToDeviceCommand converterDeviceToDeviceCommand) {
        this.deviceCommand = deviceCommand;
        this.converterDeviceToDeviceCommand = converterDeviceToDeviceCommand;
    }

    public RegistrationCommand registerCollector(Device device) {

        final WebClient client = WebClient.builder().baseUrl(edgeURI).build();

        DeviceCommand dc = converterDeviceToDeviceCommand.convert(device);

        Mono<RegistrationCommand> result = client
                .post()
                .uri(baseRegistrationURI + "/device/new")
                .body(BodyInserters.fromObject(dc))
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(RegistrationCommand.class)
                .onErrorReturn(new RegistrationCommand());

        return result.block();
    }

    public RegistrationCommand isRegistered(String uniqueid) {
        final WebClient client = WebClient.builder().baseUrl(edgeURI).build();

        Mono<RegistrationCommand> result = client
                .post()
                .uri(baseRegistrationURI + "/device")
                .body(BodyInserters.fromObject(uniqueid))
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(RegistrationCommand.class);

        return result.block();
    }
}
