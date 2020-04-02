package eu.seatter.homemeasurement.collector.services.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import eu.seatter.homemeasurement.collector.cache.AlertSystemCache;
import eu.seatter.homemeasurement.collector.cache.map.AlertSystemCacheMapImpl;
import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.model.MeasurementAlert;
import eu.seatter.homemeasurement.collector.model.SystemAlert;
import eu.seatter.homemeasurement.collector.services.cache.MQMeasurementCacheService;
import eu.seatter.homemeasurement.collector.services.cache.MeasurementCacheService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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

    //private final AlertService alertService;

    private final String MQ_HOST;
    private final int MQ_PORT;
    private final String MQ_VHOST;
    private final String MQ_EXCHANGE_NAME;
    private final String MQ_USERNAME;
    private final String MQ_USERPASSWORD;
    private final MessageStatus messageStatus;
    private final AlertSystemCache alertSystemCache;
    private final MeasurementCacheService measurementCacheService;
    private final MQMeasurementCacheService mqMeasurementCacheService;

    public RabbitMQService(//AlertService alertService,
                           @Value("${RabbitMQService.hostname:localhost}") String hostname,
                           @Value("${RabbitMQService.portnumber:5672}") int portnumber,
                           @Value("${RabbitMQService.vhost:/}") String vhost,
                           @Value("${RabbitMQService.exchangee:}") String exchangename,
                           @Value("${RabbitMQService.username:}") String username,
                           @Value("${RabbitMQService.password:}") String password,
                           MessageStatus messageStatus,
                           AlertSystemCacheMapImpl alertSystemCache,
                           MeasurementCacheService measurementCacheService,
                           MQMeasurementCacheService mqMeasurementCacheService, Environment env) {
        //this.alertService = alertService;
        this.MQ_HOST = hostname;
        this.MQ_PORT = portnumber;
        this.MQ_VHOST = vhost;
        this.MQ_EXCHANGE_NAME = exchangename;
        this.MQ_USERNAME = username;
        this.MQ_USERPASSWORD = password;
        this.messageStatus = messageStatus;
        this.alertSystemCache = alertSystemCache;
        this.measurementCacheService = measurementCacheService;
        this.mqMeasurementCacheService = mqMeasurementCacheService;

        log.debug("Host     :" + MQ_HOST);
        log.debug("Port     :" + MQ_PORT);
        log.debug("VHost    :" + MQ_VHOST);
        log.debug("ExName   :" + MQ_EXCHANGE_NAME);
        this.env = env;
    }

    @Override
    public boolean sendMeasurement(Measurement measurement) {

        try {
            String messagesToEmit = convertToJSONMessage(measurement);
            sendMessage(messagesToEmit,"measurement");
            measurement.setMeasurementSentToMq(true);
            //measurementCacheService.measurementSentToMq(measurement.getRecordUID(), true);
            return true;
        } catch (MessagingException | JsonProcessingException ex) {
            messageSendFailed(ex.getMessage(), measurement);
            mqMeasurementCacheService.add(measurement);
        }
        return false;
    }

    @Override
    public boolean sendMeasurementAlert(MeasurementAlert measurementAlert) throws MessagingException {
        try {
            String messagesToEmit = convertToJSONMessage(measurementAlert);
            sendMessage(messagesToEmit, "alertmeasurement");
            //measurementCacheService.alertSentToMq(measurement.getRecordUID(), true);
        } catch (MessagingException | JsonProcessingException ex) {
            messageSendFailed(ex.getMessage());
        }
        return true;
    }

    @Override
    public boolean sendSystemAlert(SystemAlert systemAlert) {
        return false;
    }

    private void sendMessage(String messagesToEmit, @NotNull String messageType) throws MessagingException {
        String MQ_QUEUE_NAME="CHANGE_ME";
        String MQ_ROUTING_KEY="CHANGE_ME";
        switch (messageType) {
            case "measurement" : {
                MQ_QUEUE_NAME = env.getProperty("RabbitMQService.queue.measurement");
                MQ_ROUTING_KEY = env.getProperty("RabbitMQService.routing_key.measurement");
                break;
            }
            case "alertmeasurement" : {
                MQ_QUEUE_NAME = env.getProperty("RabbitMQService.queue.alert.measurement");
                MQ_ROUTING_KEY = env.getProperty("RabbitMQService.routing_key.alert.measurement");
                break;
            }
            case "alertsystem" : {
                MQ_QUEUE_NAME = env.getProperty("RabbitMQService.queue.alert.system");
                MQ_ROUTING_KEY = env.getProperty("RabbitMQService.routing_key.alert.system");
                break;
            }
        }
        log.debug("QName    :" + MQ_QUEUE_NAME);
        log.debug("RoutKey  :" + MQ_ROUTING_KEY);

        log.info("MQ Sending measurement message");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(MQ_HOST);
        factory.setPort(MQ_PORT);
        factory.setUsername(MQ_USERNAME);
        factory.setPassword(MQ_USERPASSWORD);
        factory.setVirtualHost(MQ_VHOST);

        log.debug("Connecting to MQ Server - " + MQ_QUEUE_NAME + "/" + MQ_VHOST + "/" + MQ_HOST + ":" + MQ_PORT);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            try {
                channel.queueDeclarePassive(MQ_QUEUE_NAME);
            } catch (IOException ex) {
                String errorMessage = "ERROR: Failed to connect to MQ Server";
                throw new MessagingException(errorMessage);
            }
            log.debug("Connected to MQ Server");

            try {
                channel.basicPublish(MQ_EXCHANGE_NAME, MQ_ROUTING_KEY, null, messagesToEmit.getBytes(StandardCharsets.UTF_8));
                log.info("MQ Sent message");
                log.debug("MQ Message : " + messagesToEmit);
                messageStatus.update(MessageStatusType.GOOD);
            } catch (IOException ex) {
                String errorMessage = "Failed to publish message to RabbitMQ Exchange :" + MQ_EXCHANGE_NAME;
                throw new MessagingException(errorMessage);
            }

        } catch (Exception ex) {
            String errorMessage = "Failed to connect to RabbitMQ host : " + MQ_HOST + " on port " + MQ_PORT;
            throw new MessagingException(errorMessage);
        }
    }

    private <T> String convertToJSONMessage(T record) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
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
//        if(measurement != null) {
//            alertService.sendMeasurementAlert(measurement, "Rabbit MQ Message Send Fail Alert", message);
//        }
    }

}
