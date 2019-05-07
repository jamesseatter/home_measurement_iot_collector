package eu.seatter.homemeasurement.collector.services;

import eu.seatter.homemeasurement.collector.cache.MeasurementCacheImpl;
import eu.seatter.homemeasurement.collector.model.SensorMeasurementUnit;
import eu.seatter.homemeasurement.collector.model.SensorRecord;
import eu.seatter.homemeasurement.collector.services.alert.EmailAlertService;
import eu.seatter.homemeasurement.collector.services.alert.MailMessageAlertMeasurement;
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

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 08/12/2018
 * Time: 15:15
 */
@Service
@Slf4j
public class CollectorService implements CommandLineRunner {
    private final MeasurementCacheImpl measurementCache;

    @Value("${measurement.interval.seconds:360}")
    private int readIntervalSeconds = 10;

    @Value("${measurement.temperature.alert.threshold:50}")
    private double temperatureAlertThreshold;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private final boolean mqEnabled;

    private final SensorMeasurement sensorMeasurement;
    private final SensorListService sensorListService;
    private final EmailAlertService alertService;
    private final SensorMessaging mqService;

    public CollectorService(MeasurementCacheImpl measurementCache, SensorMeasurement sensorMeasurement, SensorListService sensorListService, EmailAlertService alertService, RabbitMQService mqService, @Value("${RabbitMQService.enabled:false}") boolean enabled) {
        this.measurementCache = measurementCache;
        this.sensorMeasurement = sensorMeasurement;
        this.sensorListService = sensorListService;
        this.alertService = alertService;
        this.mqService = mqService;
        this.mqEnabled = enabled;
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
                measurementCache.add(sr);

                if (mqEnabled) {
                    mqService.sendMeasurement(sr);
                }
                try {
                    AlertOnThresholdExceeded(sr);
                } catch (Exception ex) {
                    log.error("Email not sent : " + ex.getLocalizedMessage());
                }
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
        if(sensorRecord.getValue() <= temperatureAlertThreshold) {
            log.debug("Sensor value below threshold. Measurement : " + sensorRecord.getValue() + " / Threshold : " + temperatureAlertThreshold);
            try {
                log.info("Sending alert email to " + "***REMOVED***");
                alertService.sendAlert(new MailMessageAlertMeasurement(sensorRecord));
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
            srec.setValue(50.0);
        }

        return Collections.unmodifiableList(sensorList);
    }
}
