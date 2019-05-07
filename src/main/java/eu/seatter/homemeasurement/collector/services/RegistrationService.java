package eu.seatter.homemeasurement.collector.services;

import eu.seatter.homemeasurement.collector.commands.DeviceCommand;
import eu.seatter.homemeasurement.collector.commands.RegistrationCommand;
import eu.seatter.homemeasurement.collector.commands.SensorCommand;
import eu.seatter.homemeasurement.collector.converters.DeviceToDeviceCommand;
import eu.seatter.homemeasurement.collector.converters.SensorRecordToSensorCommand;
import eu.seatter.homemeasurement.collector.model.Device;
import eu.seatter.homemeasurement.collector.model.SensorRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 29/01/2019
 * Time: 23:14
 */
@Service
@Slf4j
public class RegistrationService {

    @Value("${rest.edge.uri.base:http://127.0.0.1}")
    private String edgeURI;

    @Value("${rest.edge.uri.api.registration:/api/v1/registration}")
    private String baseRegistrationURI;

    private final DeviceToDeviceCommand converterDeviceToDeviceCommand;
    private final SensorRecordToSensorCommand converterSensorRecordToSensorCommand;

    public RegistrationService(DeviceToDeviceCommand converterDeviceToDeviceCommand, SensorRecordToSensorCommand converterSensorRecordToSensorCommand) {
        this.converterDeviceToDeviceCommand = converterDeviceToDeviceCommand;
        this.converterSensorRecordToSensorCommand = converterSensorRecordToSensorCommand;
    }

    public RegistrationCommand registerCollector(Device device) {

        final WebClient client = WebClient.builder().baseUrl(edgeURI).build();

        DeviceCommand dc = converterDeviceToDeviceCommand.convert(device);

        Mono<RegistrationCommand> result = client
                .post()
                .uri(baseRegistrationURI + "/device/new")
                .body(BodyInserters.fromObject(Objects.requireNonNull(dc)))
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(RegistrationCommand.class)
                .onErrorReturn(new RegistrationCommand());

        return result.block();
    }

    public RegistrationCommand isCollectorRegistered(String uniqueid) {
        final WebClient webClient = WebClient.builder().build();


        Mono<RegistrationCommand> result = webClient.get()
                                            .uri(uriBuilder -> uriBuilder.path(edgeURI + " / " + baseRegistrationURI + "/device")
                                                    .queryParam("uniqueid", uniqueid)
                                                    .build())
                                            .accept(APPLICATION_JSON)
                                            .retrieve()
//                                            .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new MyCustomException()))
//                                            .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new MyCustomException()))
                                            .bodyToMono(RegistrationCommand.class)
                                            .retry(5);

        //RegistrationCommand registeredDevice = new RegistrationCommand();

        return result.block();
    }

    public SensorCommand registerSensors(List<SensorRecord> sensorRecordList) {

        final WebClient client = WebClient.builder().baseUrl(edgeURI).build();

        List<SensorCommand> sensorCommandList = new ArrayList<>();
        for(SensorRecord sr : sensorRecordList) {
            sensorCommandList.add(converterSensorRecordToSensorCommand.convert(sr));
        }

        Mono<SensorCommand> result = client
                .post()
                .uri(baseRegistrationURI + "/sensors/new")
                .body(BodyInserters.fromObject(sensorCommandList))
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(SensorCommand.class)
                .onErrorReturn(new SensorCommand());

        return result.block();
    }
}
