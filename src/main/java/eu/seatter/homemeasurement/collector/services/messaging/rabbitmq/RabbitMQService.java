package eu.seatter.homemeasurement.collector.services.messaging.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.seatter.homemeasurement.collector.config.RabbitMQConfig;
import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.model.MeasurementAlert;
import eu.seatter.homemeasurement.collector.model.SystemAlert;
import eu.seatter.homemeasurement.collector.services.cache.AlertSystemCacheService;
import eu.seatter.homemeasurement.collector.services.cache.MQMeasurementCacheService;
import eu.seatter.homemeasurement.collector.services.messaging.Converter;
import eu.seatter.homemeasurement.collector.services.messaging.MessageStatus;
import eu.seatter.homemeasurement.collector.services.messaging.MessageStatusType;
import eu.seatter.homemeasurement.collector.services.messaging.SensorMessaging;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 24/03/2019
 * Time: 12:13
 */

@Slf4j
@Service
public class RabbitMQService implements SensorMessaging {
    @Value("#{new Boolean('${rabbitmqservice.enabled:false}')}")
    boolean mqEnabled;

    private final MessageStatus messageStatus;
    private final AlertSystemCacheService alertSystemCacheService;
    private final MQMeasurementCacheService mqMeasurementCacheService;
    private final AmqpTemplate rabbitTemplate;
    private final RabbitMQConfig rabbitMQConfig;
    private final Converter converter;

    public RabbitMQService(MessageStatus messageStatus,
                           AlertSystemCacheService alertSystemCache,
                           MQMeasurementCacheService mqMeasurementCacheService,
                           AmqpTemplate rabbitTemplate,
                           RabbitMQConfig rabbitMQConfig,
                           Converter converter) {
        this.messageStatus = messageStatus;
        this.alertSystemCacheService = alertSystemCache;
        this.mqMeasurementCacheService = mqMeasurementCacheService;
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitMQConfig = rabbitMQConfig;
        this.converter = converter;
    }

    @Override
    public boolean sendMeasurement(Measurement measurement) {

        try {
            String messagesToEmit = converter.convertToJSONMessage(measurement);
            sendMessage(messagesToEmit,"measurement");
            measurement.setMeasurementSentToMq(true);
            return true;
        } catch (AmqpException | JsonProcessingException ex) {
            messageSendFailed(ex.getMessage(), measurement);
            mqMeasurementCacheService.add(measurement);
        }
        return false;
    }

    @Override
    public boolean sendMeasurementAlert(MeasurementAlert measurementAlert) {
        try {
            String messagesToEmit = converter.convertToJSONMessage(measurementAlert);
            sendMessage(messagesToEmit, "alertmeasurement");
        } catch (AmqpException | JsonProcessingException ex) {
            messageSendFailed(ex.getMessage());
        }
        return true;
    }

    @Override
    public boolean sendSystemAlert(SystemAlert systemAlert) {
        try {
            String messagesToEmit = converter.convertToJSONMessage(systemAlert);
            sendMessage(messagesToEmit, "alertsystem");
        } catch (AmqpException | JsonProcessingException ex) {
            messageSendFailed(ex.getMessage());
        }
        return true;
    }

    @Override
    public boolean flushCache() {
        if(mqEnabled && mqMeasurementCacheService.getCacheSize() > 0) {
            log.warn("MQ cache has entries that must be sent to the MQ server.");
            List<Measurement> mqmeasurements = mqMeasurementCacheService.getAll();
            log.warn("MQ cache has " + mqmeasurements.size() + " entries that must be sent to the MQ server.");
            Iterator<Measurement> iter = mqmeasurements.iterator();
            while(iter.hasNext()) {
                Measurement m = iter.next();
                if(sendMeasurement(m)) {
                    iter.remove();
                    log.info("Record " +  m.getRecordUID() + " sent to MQ");
                }
            }
            try {
                mqMeasurementCacheService.flushToFile();
            } catch (IOException ex) {
                log.error("Error flushing the MQ Cache to disk : " + ex.getMessage());
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    private void sendMessage(String messagesToEmit, @NotNull String messageType) throws AmqpException {
        if(mqEnabled) {
            String mqroutingkey;
            switch (messageType) {
                case "measurement" :
                    mqroutingkey = rabbitMQConfig.getRoutingkey();
                    break;
                case "alertmeasurement" :
                    mqroutingkey = rabbitMQConfig.getRoutingkeyAlertMeasurement();
                    break;
                case "alertsystem" :
                    mqroutingkey = rabbitMQConfig.getRoutingkeyAlertSystem();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + messageType);
            }
            if (mqroutingkey == null) throw new AssertionError();

            log.info("MQ Sending measurement message to : " +

                    rabbitMQConfig.getHost() +
                    "/" + rabbitMQConfig.getVirtualHost() +
                    "/" + rabbitMQConfig.getExchange() +
                    " key : " + mqroutingkey);
            try {
                rabbitTemplate.convertAndSend(rabbitMQConfig.getExchange(), mqroutingkey, messagesToEmit);
                log.info("MQ measurement message sent");
            } catch (AmqpException ex) {
                log.error("Failed to send message to MQ : " + ex.getLocalizedMessage());
                throw (ex);
            }
        } else {
            log.info("MQ Service is disabled, no message sent.");
        }
    }



    private void messageSendFailed(String message) {
        messageSendFailed(message,Measurement.builder().build());
    }

    private void messageSendFailed(String message, Measurement measurement) {
        log.error(message);
        log.error("Failed measurement: " + measurement.toString());
        messageStatus.update(MessageStatusType.ERROR);
        alertSystemCacheService.add("Rabbit MQ", message);
    }

}
