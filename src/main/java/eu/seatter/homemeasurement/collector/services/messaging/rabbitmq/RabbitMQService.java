package eu.seatter.homemeasurement.collector.services.messaging.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.seatter.homemeasurement.collector.config.RabbitMQProperties;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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
    private final Environment env;
    @Value("#{new Boolean('${rabbitmqservice.enabled:false}')}")
    boolean mqEnabled;

    private final MessageStatus messageStatus;
    private final AlertSystemCacheService alertSystemCacheService;
    private final MQMeasurementCacheService mqMeasurementCacheService;
    private final RabbitTemplate rabbitTemplate;
    private final Converter converter;

    final RabbitMQProperties rabbitMQProperties;

    public RabbitMQService(MessageStatus messageStatus,
                           AlertSystemCacheService alertSystemCache,
                           MQMeasurementCacheService mqMeasurementCacheService,
                           Environment env,
                           RabbitTemplate rabbitTemplate,
                           RabbitMQProperties rabbitMQProperties,
                           Converter converter) {
        this.messageStatus = messageStatus;
        this.alertSystemCacheService = alertSystemCache;
        this.mqMeasurementCacheService = mqMeasurementCacheService;
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitMQProperties = rabbitMQProperties;
        this.env = env;
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
                    mqroutingkey = env.getProperty("rabbitmqservice.routing_key.measurement");
                    break;
                case "alertmeasurement" :
                    mqroutingkey = env.getProperty("rabbitmqservice.routing_key.alert.measurement");
                    break;
                case "alertsystem" :
                    mqroutingkey = env.getProperty("rabbitmqservice.routing_key.alert.system");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + messageType);
            }
            if (mqroutingkey == null) throw new AssertionError();

            log.info("MQ Sending measurement message to : " +
                    rabbitTemplate.getConnectionFactory().getHost() +
                    ":" + rabbitTemplate.getConnectionFactory().getPort() +
                    "/" + rabbitTemplate.getConnectionFactory().getVirtualHost() +
                    "/" + rabbitMQProperties.getExchange() +
                    " key : " + mqroutingkey);
            try {
                rabbitTemplate.convertAndSend(rabbitMQProperties.getExchange(), mqroutingkey, messagesToEmit);
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
