package eu.seatter.homemeasurement.collector.services;

import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.services.alert.AlertService;
import eu.seatter.homemeasurement.collector.services.cache.MeasurementCacheService;
import eu.seatter.homemeasurement.collector.services.messaging.azure.AzureIOTHub;
import eu.seatter.homemeasurement.collector.services.messaging.rabbitmq.RabbitMQService;
import eu.seatter.homemeasurement.collector.services.sensor.SensorMeasurement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.List;

@Service
@Slf4j
public class ProcessSensor {
    private final MeasurementCacheService measurementCacheService;
    private final RabbitMQService mqService;
    private final AzureIOTHub azureIOTHub;
    private final AlertService alertService;
    private final SensorMeasurement sensorMeasurement;


    public ProcessSensor(MeasurementCacheService measurementCacheService, RabbitMQService mqService, AzureIOTHub azureIOTHub, AlertService alertService, SensorMeasurement sensorMeasurement) {
        this.measurementCacheService = measurementCacheService;
        this.mqService = mqService;
        this.azureIOTHub = azureIOTHub;
        this.alertService = alertService;
        this.sensorMeasurement = sensorMeasurement;
    }

    public void process(List<Measurement> sensorList) {
        log.debug("Perform sensor measurements");
        List<Measurement> measurements  = sensorMeasurement.collect(sensorList);

        for (Measurement sr : measurements) {
            measurementCacheService.add(sr);
            mqService.sendMeasurement(sr);
            azureIOTHub.sendMeasurement(sr);
            alertOnThresholdExceeded(sr);
        }
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
