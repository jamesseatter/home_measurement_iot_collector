package eu.seatter.homemeasurement.collector.services;

import eu.seatter.homemeasurement.collector.exception.SensorNotFoundException;
import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.services.alert.AlertService;
import eu.seatter.homemeasurement.collector.services.cache.CacheLoad;
import eu.seatter.homemeasurement.collector.services.cache.MQMeasurementCacheService;
import eu.seatter.homemeasurement.collector.services.cache.MeasurementCacheService;
import eu.seatter.homemeasurement.collector.services.messaging.RabbitMQService;
import eu.seatter.homemeasurement.collector.services.messaging.SensorMessaging;
import eu.seatter.homemeasurement.collector.services.sensor.SensorListService;
import eu.seatter.homemeasurement.collector.services.sensor.SensorMeasurement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static eu.seatter.homemeasurement.collector.services.TestData.testData;
import static eu.seatter.homemeasurement.collector.services.TestData.testSensorList;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 08/12/2018
 * Time: 15:15
 */
@Service
@Slf4j
public class CollectorService implements CommandLineRunner {
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    private final long readIntervalSeconds;
//    private final boolean mqEnabled;

    private final CacheLoad cacheLoad;
    private final MeasurementCacheService measurementCacheService;
    private final MQMeasurementCacheService mqMeasurementCacheService;
    private final SensorMeasurement sensorMeasurement;
    private final SensorListService sensorListService;
    private final AlertService alertService;
    private final SensorMessaging mqService;

    public CollectorService(
            CacheLoad cacheLoad,
            SensorMeasurement sensorMeasurement,
            SensorListService sensorListService,
            AlertService alertService,
            MeasurementCacheService measurementCacheService,
            RabbitMQService mqService,
            @Value("${measurement.interval.seconds:360}") long readIntervalSeconds,
//            @Value("#{new Boolean('${rabbitmqservice.enabled:false}')}") Boolean mqEnabled,
            MQMeasurementCacheService mqMeasurementCacheService) {
                    this.cacheLoad = cacheLoad;
                    this.sensorMeasurement = sensorMeasurement;
                    this.sensorListService = sensorListService;
                    this.alertService = alertService;
                    this.measurementCacheService = measurementCacheService;
                    this.mqService = mqService;
//                    this.mqEnabled = mqEnabled;
                    this.readIntervalSeconds = readIntervalSeconds;
                    this.mqMeasurementCacheService = mqMeasurementCacheService;
    }

    @Override
    public void run(String... strings) throws MessagingException {
        boolean running = true;
        log.info("Sensor Read Interval (seconds) : "+ readIntervalSeconds);

        List<Measurement> sensorList = Collections.emptyList();
        List<Measurement> measurements = new ArrayList<>();

        try {
            if(activeProfile.equals("dev")) {
                log.warn("Add Dev sensor list");
                sensorList = testSensorList();
            } else {
                log.debug("Perform sensor search");
                sensorList = sensorListService.getSensors();
            }
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

            if(activeProfile.equals("dev")) {
                log.warn("Add Dev sensor measurements");
                measurements.clear();
                measurements.addAll(testData(sensorList));
            } else {
                log.debug("Perform sensor measurements");
                measurements.clear();
                measurements = sensorMeasurement.collect(sensorList);
            }

            for (Measurement sr : measurements){
                measurementCacheService.add(sr);
                mqService.sendMeasurement(sr);
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
