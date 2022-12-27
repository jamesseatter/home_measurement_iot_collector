package eu.seatter.homemeasurement.collector.services.sensor;

import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.model.enums.SensorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 05/02/2019
 * Time: 11:35
 */
@Slf4j
@Service
@Profile("dev")
public class SensorListServiceDev implements SensorListService {

    @Override
    public List<Measurement> getSensors() {
        log.warn("Test sensor list in use");
        List<Measurement> list = new ArrayList<>();
        list.add(Measurement.builder()
                .recordUID(UUID.randomUUID())
                .sensorid("28-000000000001")
                .title("Température de l'eau à l'arrivée")
                .shortTitle("Arrive")
                .description("Returns the temperature of the hot water entering the house from the central heating system")
                .familyid(40)
                .sensortype(SensorType.ONEWIRE)
                .low_threshold(45.0)
                .high_threshold(60.0)
                .alertgroup("temperature_threshold_alerts_private")
                .alertdestination("BORRY")
                .build());

        list.add(Measurement.builder()
                .recordUID(UUID.randomUUID())
                .sensorid("28-000000000002")
                .title("Température de l'eau de chaudière")
                .shortTitle("Boiler")
                .description("Returns the temperature of the hot water in the boiler")
                .familyid(40)
                .sensortype(SensorType.ONEWIRE)
                .low_threshold(35.0)
                .high_threshold(60.0)
                .alertgroup("temperature_threshold_alerts_private")
                .alertdestination("PRIVATE")
                .build());

        return list;
    }
}
