package eu.seatter.homemeasurement.collector.services;

import eu.seatter.homemeasurement.collector.exception.SensorNotFoundException;
import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.services.cache.CacheLoad;
import eu.seatter.homemeasurement.collector.services.messaging.SensorMessaging;
import eu.seatter.homemeasurement.collector.services.messaging.rabbitmq.RabbitMQService;
import eu.seatter.homemeasurement.collector.services.sensor.SensorListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 08/12/2018
 * Time: 15:15
 */
@Service
@Slf4j
public class CollectorService implements CommandLineRunner {

    private final SensorListService sensorListService;
    private final CacheLoad cacheLoad;

    private final SensorMessaging mqService;
    private final ProcessSensor processSensor;

    private final long readIntervalSeconds;


    public CollectorService(
            CacheLoad cacheLoad,
            RabbitMQService mqService,
            @Value("${measurement.interval.seconds:360}") long readIntervalSeconds, SensorListService sensorListService, ProcessSensor processSensor) {
                    this.cacheLoad = cacheLoad;
                    this.mqService = mqService;
                    this.readIntervalSeconds = readIntervalSeconds;
                    this.sensorListService = sensorListService;
                    this.processSensor = processSensor;
    }

    @Override
    public void run(String... strings) {
        boolean running = true;
        log.info("Sensor Read Interval (seconds) : "+ readIntervalSeconds);

        List<Measurement> sensorList;

        try {
            sensorList = sensorListService.getSensors();
            log.debug("Sensor count : " + sensorList.size());
        } catch (RuntimeException ex) {
            log.warn("No sensors were connected to the system. Shutting down");
            throw new SensorNotFoundException("No sensors found",
                    "Verify that sensors are connected to the device and try to restart the service. Verify the logs show the sensors being found.");
        }

        //load cache's
        cacheLoad.load();

        while(running) {
            mqService.flushCache();

            if (sensorList.isEmpty()) {
                log.info("No sensors connected to device. Exiting.");
                break;
            } else {
                running = true;
            }

            processSensor.process(sensorList);

            try {
                log.info("Next measurement in " + readIntervalSeconds + " seconds");
                Thread.sleep(readIntervalSeconds * 1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                log.error(ex.getLocalizedMessage());
                Thread.currentThread().interrupt();
                running = false;
            }
        }
        log.info("Execution stopping");
    }
}
