package eu.seatter.homemeasurement.collector.services;

import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.services.alert.AlertService;
import eu.seatter.homemeasurement.collector.services.cache.AlertCacheService;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private final int readIntervalSeconds;
    private final Boolean mqEnabled;

    private final MeasurementCacheService measurementCacheService;
    private final MQMeasurementCacheService mqMeasurementCacheService;
    private final AlertCacheService alertCacheService;
    private final SensorMeasurement sensorMeasurement;
    private final SensorListService sensorListService;
    private final AlertService alertService;
    private final SensorMessaging mqService;

    public CollectorService(
            SensorMeasurement sensorMeasurement,
            SensorListService sensorListService,
            AlertService alertService,
            MeasurementCacheService measurementCacheService,
            RabbitMQService mqService,
            @Value("${measurement.interval.seconds:360}") int readIntervalSeconds,
            @Value("#{new Boolean('${RabbitMQService.enabled:false}')}") Boolean mqEnabled,
            MQMeasurementCacheService mqMeasurementCacheService, AlertCacheService alertCacheService) {
                    this.sensorMeasurement = sensorMeasurement;
                    this.sensorListService = sensorListService;
                    this.alertService = alertService;
                    this.measurementCacheService = measurementCacheService;
                    this.mqService = mqService;
                    this.mqEnabled = mqEnabled;
                    this.readIntervalSeconds = readIntervalSeconds;
                    this.mqMeasurementCacheService = mqMeasurementCacheService;
        this.alertCacheService = alertCacheService;
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
            //throw new SensorNotFoundException("No sensors found",
//                    "Verify that sensors are connected to the device and try to restart the service. Verify the logs show the sensors being found.");
        }

        //load cache's
        loadCaches();

        while(running) {
            //If the mqcache has entries, try to sent them to MQ
            if(mqEnabled) {
                if (mqMeasurementCacheService.getCacheSize() > 0) {
                    log.warn("MQ cache has entries that must be sent to the MQ server.");
                    List<Measurement> mqmeasurements = mqMeasurementCacheService.getAll();
                    log.warn("MQ cache has " + mqmeasurements.size() + " entries that must be sent to the MQ server.");
                    Iterator iter = mqmeasurements.iterator();
                    while(iter.hasNext()) {
                        Measurement m = (Measurement) iter.next();
                        boolean result = mqService.sendMeasurement(m);
                        if (result) {
                            iter.remove();
                            log.info("Record " +  m.getRecordUID() + " sent to MQ");
                        }
                    }
                    try {
                        mqMeasurementCacheService.flushToFile();
                    } catch (IOException ex) {
                        log.error("Error flushing the MQ Cache to disk : " + ex.getMessage());
                    }
                }
            }
            if(sensorList.size() > 0) {
                running = true;
                //todo Send sensor list to the Edge
            } else {
                log.info("No sensors connected to device. Exiting.");
                break;
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
                if (mqEnabled) {
                    mqService.sendMeasurement(sr);
                }
                AlertOnThresholdExceeded(sr);
            }

            try {
                Thread.sleep(readIntervalSeconds * 1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                log.error(ex.getLocalizedMessage());
                running = false;
            }
        }
        log.info("Execution stopping");
    }

    private void AlertOnThresholdExceeded(Measurement measurement) {
        //todo check getlow_threshold is defined
        if(measurement.getLow_threshold() != null && measurement.getValue() <= measurement.getLow_threshold()) {
            log.debug("Sensor value below threshold. Measurement : " + measurement.getValue() + " / Threshold : " + measurement.getLow_threshold());
            try {
                log.info("Sending alert email");
                String alertTitle = "Sensor below threshold";
                String alertMessage = "The sensor \"" + measurement.getDescription() + "\" has a reading of " + measurement.getValue()+measurement.getMeasurementUnit() + " which is below " + measurement.getLow_threshold();

                alertService.sendMeasurementAlert(measurement, alertTitle, alertMessage);
            } catch (MessagingException e) {
                log.error("Failed to send Email Alert : " + e.getLocalizedMessage());
            } catch (Exception e) {
                log.error("A general error occurred : " + e.getLocalizedMessage());
            }
        }
    }

    private void loadCaches() {
        try {
            log.info("Load mq cache");
            int count = mqMeasurementCacheService.readFromFile();
            log.info("Loaded " + count + " mq records");
        } catch (Exception ex) {
            log.error("Error loading cached mq entries from file. No cached data will be loaded : " + ex.getMessage());
        }
        try {
            log.info("Load measurement cache");
            int count = measurementCacheService.readFromFile();
            log.info("Loaded " + count + " measurement records");
        } catch (Exception ex) {
            log.error("Error loading cached measurement entries from file. No cached data will be loaded : " + ex.getMessage());
        }
//        try {
//            log.info("Load system alert cache");
//            int count = alertCacheService.readFromFile();
//            log.info("Loaded " + count + " alert records");
//        } catch (Exception ex) {
//            log.error("Error loading cached system alert entries from file. No cached data will be loaded : " + ex.getMessage());
//        }
    }


}
