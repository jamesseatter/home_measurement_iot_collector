package eu.seatter.homemeasurement.collector.services;

import eu.seatter.homemeasurement.collector.model.SensorMeasurementUnit;
import eu.seatter.homemeasurement.collector.model.SensorRecord;
import eu.seatter.homemeasurement.collector.model.SensorType;
import eu.seatter.homemeasurement.collector.services.alert.AlertService;
import eu.seatter.homemeasurement.collector.services.alert.AlertServiceMeasurement;
import eu.seatter.homemeasurement.collector.services.cache.CacheService;
import eu.seatter.homemeasurement.collector.services.messaging.RabbitMQService;
import eu.seatter.homemeasurement.collector.services.messaging.SensorMessaging;
import eu.seatter.homemeasurement.collector.services.sensor.SensorListService;
import eu.seatter.homemeasurement.collector.services.sensor.SensorMeasurement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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

    private final CacheService cacheService;
    private final SensorMeasurement sensorMeasurement;
    private final SensorListService sensorListService;
    private final AlertService alertService;
    private final SensorMessaging mqService;

    public CollectorService(
            SensorMeasurement sensorMeasurement,
            SensorListService sensorListService,
            AlertServiceMeasurement alertService,
            RabbitMQService mqService,
            @Value("${measurement.interval.seconds:360}") int readIntervalSeconds,
            @Value("#{new Boolean('${RabbitMQService.enabled:false}')}") Boolean mqEnabled,
            @Value("#{new Boolean('${message.alert.enabled:false}')}") Boolean alertEnabled, CacheService cacheService) {
                    this.sensorMeasurement = sensorMeasurement;
                    this.sensorListService = sensorListService;
                    this.alertService = alertService;
                    this.mqService = mqService;
                    this.mqEnabled = mqEnabled;
                    this.readIntervalSeconds = readIntervalSeconds;
                    this.cacheService = cacheService;
                }

    @Override
    public void run(String... strings) {
        boolean running = true;
//        try {
//            deviceService.registerDevice();
//        } catch (RuntimeException ex) {
//
//        }

        log.info("Sensor Read Interval (seconds) : "+ readIntervalSeconds);

        List<SensorRecord> sensorList = Collections.emptyList();
        List<SensorRecord> measurements = new ArrayList<>();

        while(running) {
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

            if(sensorList.size() > 0) {
                running = true;
                //todo Send sensor list to the Edge
            } else {
                log.info("No sensors connected to device. Exiting.");
                break;
            }

            if(activeProfile.equals("dev")) {
                log.warn("Add Dev sensor measurements");
                measurements.addAll(testData(sensorList));
            } else {
                log.debug("Perform sensor measurements");
                measurements.clear();
                measurements = sensorMeasurement.collect(sensorList);
            }


            for (SensorRecord sr : measurements){
                cacheService.add(sr);
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

    private void AlertOnThresholdExceeded(SensorRecord sensorRecord) {
        //todo check getlow_threshold is defined
        if(sensorRecord.getValue() <= sensorRecord.getLow_threshold()) {
            log.debug("Sensor value below threshold. Measurement : " + sensorRecord.getValue() + " / Threshold : " + sensorRecord.getLow_threshold());
            try {
                log.info("Sending alert email");
                alertService.sendAlert(sensorRecord,"Sensor Value below threshold");
            } catch (MessagingException e) {
                log.error("Failed to send Email Alert : " + e.getLocalizedMessage());
            } catch (Exception e) {
                log.error("A general error occured : " + e.getLocalizedMessage());
            }
        }

    }

    private List<SensorRecord> testSensorList() {
        log.warn("Test sensor list in use");
        List<SensorRecord> list = new ArrayList<>();
        SensorRecord sr = new SensorRecord();
        list.add(SensorRecord.builder()
                .sensorid("28-000000000001")
                .title("Température de l'eau à l'arrivée")
                .description("Returns the temperature of the hot water entering the house from the central heating system")
                .familyid(40)
                .sensorType(SensorType.ONEWIRE)
                .low_threshold(45.0)
                .high_threshold(60.0)
                .alertgroup("temperature_threshold_alerts_private")
                .alertdestination("BORRY")
                .build());

        list.add(SensorRecord.builder()
                .sensorid("28-000000000002")
                .title("Température de l'eau de chaudière")
                .description("Returns the temperature of the hot water in the boiler")
                .familyid(40)
                .sensorType(SensorType.ONEWIRE)
                .low_threshold(35.0)
                .high_threshold(60.0)
                .alertgroup("temperature_threshold_alerts_private")
                .alertdestination("PRIVATE")
                .build());

        return list;
    }

    private List<SensorRecord> testData(List<SensorRecord> sensorList) {
        log.warn("Test measurement data in use");
        for(SensorRecord srec : sensorList) {
            srec.setMeasurementUnit(SensorMeasurementUnit.C);
            srec.setMeasureTimeUTC(ZonedDateTime.now());
            int val = ThreadLocalRandom.current().nextInt(35, 75);
            srec.setValue((double)val);
        }

        return Collections.unmodifiableList(sensorList);
    }
}
