package eu.seatter.homemeasurement.collector.services;

import eu.seatter.homemeasurement.collector.model.SensorMeasurementUnit;
import eu.seatter.homemeasurement.collector.model.SensorRecord;
import eu.seatter.homemeasurement.collector.services.alert.AlertService;
import eu.seatter.homemeasurement.collector.services.alert.AlertServiceImpl;
import eu.seatter.homemeasurement.collector.services.messaging.RabbitMQService;
import eu.seatter.homemeasurement.collector.services.messaging.SensorMessaging;
import eu.seatter.homemeasurement.collector.services.sensor.SensorListService;
import eu.seatter.homemeasurement.collector.services.sensor.SensorMeasurement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
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

    private int readIntervalSeconds;
    private final Boolean mqEnabled;
    private final Boolean alertEnabled;

    private CacheService cacheService;
    private final SensorMeasurement sensorMeasurement;
    private final SensorListService sensorListService;
    private final AlertService alertService;
    private final SensorMessaging mqService;

    public CollectorService(
            SensorMeasurement sensorMeasurement,
            SensorListService sensorListService,
            AlertServiceImpl alertService,
            RabbitMQService mqService,
            @Value("${measurement.interval.seconds:360}") int readIntervalSeconds,
            @Value("#{new Boolean('${RabbitMQService.enabled:false}')}") Boolean mqEnabled,
            @Value("#{new Boolean('${message.alert.enabled:false}')}") Boolean alertEnabled, CacheService cacheService) {
        this.sensorMeasurement = sensorMeasurement;
        this.sensorListService = sensorListService;
        this.alertService = alertService;
        this.mqService = mqService;
        this.mqEnabled = mqEnabled;
        this.alertEnabled = alertEnabled;
        this.readIntervalSeconds = readIntervalSeconds;
        this.cacheService = cacheService;
    }

    @Override
    public void run(String... strings) {
        boolean running = false;
//        try {
//            deviceService.registerDevice();
//        } catch (RuntimeException ex) {
//
//        }

        log.info("Sensor Read Interval (seconds) : "+ readIntervalSeconds);

        List<SensorRecord> sensorList = Collections.emptyList();

        try {
            log.debug("Perform sensor search");
            sensorList = sensorListService.getSensors();
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
        }

        List<SensorRecord> measurements = new ArrayList<>();
        while(running) {
            log.debug("Perform sensor measurements");
            measurements.clear();
            measurements = sensorMeasurement.collect(sensorList);

            if(activeProfile.equals("dev")) {
                measurements.addAll(testData(sensorList));
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
        if(sensorRecord.getValue() <= sensorRecord.getLow_threshold()) {
            log.debug("Sensor value below threshold. Measurement : " + sensorRecord.getValue() + " / Threshold : " + sensorRecord.getLow_threshold());
            try {
                log.info("Sending alert email");
                alertService.sendAlert(sensorRecord);
            } catch (MessagingException e) {
                log.error("Failed to send Email Alert : " + e.getLocalizedMessage());
            }
        }

    }

    private List<SensorRecord> testData(List<SensorRecord> sensorList) {
        log.warn("Setting up test measurement data");
        for(SensorRecord srec : sensorList) {
            srec.setMeasurementUnit(SensorMeasurementUnit.C);
            srec.setMeasureTimeUTC(LocalDateTime.now());
            int val = ThreadLocalRandom.current().nextInt(35, 75);
            srec.setValue((double)val);
        }

        return Collections.unmodifiableList(sensorList);
    }
}
