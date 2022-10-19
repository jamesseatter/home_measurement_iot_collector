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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
        log.info("Sensor Read Interval (seconds) : "+ readIntervalSeconds);

        //load cache's
        cacheLoad.load();

        Runnable measurementTask = () -> {
            List<Measurement> sensorList;
            log.info("Start sensor measurement cycle");
            try {
                sensorList = sensorListService.getSensors();
                log.debug("Sensor count : " + sensorList.size());
            } catch (RuntimeException ex) {
                log.warn("No sensors were connected to the system. Shutting down");
                throw new SensorNotFoundException("No sensors found",
                        "Verify that sensors are connected to the device and try to restart the service. Verify the logs show the sensors being found.");
            }

            mqService.flushCache();

            if (!sensorList.isEmpty()) {
                try {
                    log.info("Measurements will be taken every " + readIntervalSeconds + " seconds");
                    processSensor.process(sensorList);
                } catch (RuntimeException ex) {
                    log.error(ex.getLocalizedMessage());
                    log.error(Arrays.toString(ex.getStackTrace()));
                    Thread.currentThread().interrupt();
                }
            } else {
                log.info("No sensors connected to device. Exiting.");
            }
            log.info("End sensor measurement cycle");
        };

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        executorService.scheduleAtFixedRate(measurementTask, 0, readIntervalSeconds, TimeUnit.SECONDS);

    }

}
