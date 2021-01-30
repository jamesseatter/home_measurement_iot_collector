package eu.seatter.homemeasurement.collector.services.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.seatter.homemeasurement.collector.cache.AlertSystemCache;
import eu.seatter.homemeasurement.collector.cache.map.AlertSystemCacheMapImpl;
import eu.seatter.homemeasurement.collector.config.RabbitMQProperties;
import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.model.MeasurementAlert;
import eu.seatter.homemeasurement.collector.model.SystemAlert;
import eu.seatter.homemeasurement.collector.services.cache.MQMeasurementCacheService;
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
@SuppressWarnings("FieldCanBeLocal")
@Slf4j
@Service
public class RabbitMQService implements SensorMessaging {
    private final Environment env;
    @Value("#{new Boolean('${rabbitmqservice.enabled:false}')}")
    Boolean mqEnabled;

    private final MessageStatus messageStatus;
    private final AlertSystemCache alertSystemCache;
    private final MQMeasurementCacheService mqMeasurementCacheService;
    private final RabbitTemplate rabbitTemplate;

    final RabbitMQProperties rabbitMQProperties;

    public RabbitMQService(MessageStatus messageStatus,
                           AlertSystemCacheMapImpl alertSystemCache,
                           MQMeasurementCacheService mqMeasurementCacheService,
                           Environment env,
                           RabbitTemplate rabbitTemplate,
                           RabbitMQProperties rabbitMQProperties) {
        this.messageStatus = messageStatus;
        this.alertSystemCache = alertSystemCache;
        this.mqMeasurementCacheService = mqMeasurementCacheService;
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitMQProperties = rabbitMQProperties;
        this.env = env;
    }

    @Override
    public boolean sendMeasurement(Measurement measurement) {

        try {
            String messagesToEmit = convertToJSONMessage(measurement);
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
            String messagesToEmit = convertToJSONMessage(measurementAlert);
            sendMessage(messagesToEmit, "alertmeasurement");
        } catch (AmqpException | JsonProcessingException ex) {
            messageSendFailed(ex.getMessage());
        }
        return true;
    }

    @Override
    public boolean sendSystemAlert(SystemAlert systemAlert) {
        try {
            String messagesToEmit = convertToJSONMessage(systemAlert);
            sendMessage(messagesToEmit, "a");
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
        }
        return true;
    }

    private void sendMessage(String messagesToEmit, @NotNull String messageType) throws AmqpException {

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
    }

    private <T> String convertToJSONMessage(T record) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        return mapper.writeValueAsString(record);
    }

    private void messageSendFailed(String message) {
        messageSendFailed(message,null);
    }

    private void messageSendFailed(String message, Measurement measurement) {
        log.error(message);
        messageStatus.update(MessageStatusType.ERROR);
        alertSystemCache.add(message);
        //todo handle via throw in code that call rabbitmq
    }

}
