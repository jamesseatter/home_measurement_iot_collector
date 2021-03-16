package eu.seatter.homemeasurement.collector.services;

import eu.seatter.homemeasurement.collector.exception.SensorNotFoundException;
import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.services.alert.AlertService;
import eu.seatter.homemeasurement.collector.services.cache.CacheLoad;
import eu.seatter.homemeasurement.collector.services.cache.MeasurementCacheService;
import eu.seatter.homemeasurement.collector.services.messaging.SensorMessaging;
import eu.seatter.homemeasurement.collector.services.messaging.azure.AzureIOTHub;
import eu.seatter.homemeasurement.collector.services.messaging.rabbitmq.RabbitMQService;
import eu.seatter.homemeasurement.collector.services.sensor.SensorListService;
import eu.seatter.homemeasurement.collector.services.sensor.SensorMeasurement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
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

    private final long readIntervalSeconds;

    private final CacheLoad cacheLoad;
    private final MeasurementCacheService measurementCacheService;
    private final SensorMeasurement sensorMeasurement;

    private final AlertService alertService;
    private final SensorMessaging mqService;
    private final AzureIOTHub azureIOTHub;

    public CollectorService(
            CacheLoad cacheLoad,
            SensorMeasurement sensorMeasurement,
            AlertService alertService,
            MeasurementCacheService measurementCacheService,
            RabbitMQService mqService,
            @Value("${measurement.interval.seconds:360}") long readIntervalSeconds, SensorListService sensorListService, AzureIOTHub azureIOTHub) {
                    this.cacheLoad = cacheLoad;
                    this.sensorMeasurement = sensorMeasurement;
                    this.alertService = alertService;
                    this.measurementCacheService = measurementCacheService;
                    this.mqService = mqService;
                    this.readIntervalSeconds = readIntervalSeconds;
        this.sensorListService = sensorListService;
        this.azureIOTHub = azureIOTHub;
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

            log.debug("Perform sensor measurements");
            List<Measurement> measurements  = sensorMeasurement.collect(sensorList);

            for (Measurement sr : measurements){
                measurementCacheService.add(sr);
                mqService.sendMeasurement(sr);
                azureIOTHub.sendMeasurement(sr);
                alertOnThresholdExceeded(sr);
            }

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

    private void alertOnThresholdExceeded(Measurement measurement) {
        if(measurement.getLow_threshold() != null && measurement.getValue() <= measurement.getLow_threshold()) {
            log.debug("Sensor value below threshold. Measurement : " + measurement.getValue() + " / Threshold : " + measurement.getLow_threshold());
            try {
                log.info("Sending alert email");
                String alertTitle = "Sensor below threshold";
                String alertMessage = "The sensor \"" + measurement.getTitle() + "\" has a reading of " + measurement.getValue()+measurement.getMeasurementUnit() + " which is below " + measurement.getLow_threshold();

                alertService.sendMeasurementAlert(measurement, alertTitle, alertMessage);
            } catch (MessagingException e) {
                log.error("Failed to send Email Alert : " + e.getLocalizedMessage());
            } catch (Exception e) {
                log.error("A general error occurred : " + e.getLocalizedMessage());
            }
        }
    }
}
