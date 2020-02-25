package eu.seatter.homemeasurement.collector.services.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import eu.seatter.homemeasurement.collector.cache.AlertSystemCache;
import eu.seatter.homemeasurement.collector.cache.map.AlertSystemCacheImpl;
import eu.seatter.homemeasurement.collector.model.SensorRecord;
import eu.seatter.homemeasurement.collector.services.alert.AlertServiceGeneralMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    private final AlertServiceGeneralMessage alertService;

    private final String MQ_MEASUREMENT_UNIQUE_ID;
    private final String MQ_REGISTRATION_UNIQUE_ID;
    private final String MQ_HOST;
    private final int MQ_PORT;
    private final String MQ_VHOST;
    private final String MQ_EXCHANGE_NAME;
    private final String MQ_QUEUE_NAME;
    private final String MQ_USERNAME;
    private final String MQ_USERPASSWORD;
    private MessageStatus messageStatus;
    private AlertSystemCache alertSystemCache;


    public RabbitMQService(AlertServiceGeneralMessage alertService, @Value("${RabbitMQService.measurement.unique_id:CHANGE_ME}") String m_uniqueid,
                           @Value("${RabbitMQService.registration.unique_id:CHANGE_ME}") String r_uniqueid,
                           @Value("${RabbitMQService.hostname:localhost}") String hostname,
                           @Value("${RabbitMQService.portnumber:5672}") int portnumber,
                           @Value("${RabbitMQService.vhost:/}") String vhost,
                           @Value("${RabbitMQService.exchangee:}") String exchangename,
                           @Value("${RabbitMQService.queue:}") String queuename,
                           @Value("${RabbitMQService.username:}") String username,
                           @Value("${RabbitMQService.password:}") String password,
                           MessageStatus messageStatus,
                           AlertSystemCacheImpl alertSystemCache) {
        this.alertService = alertService;
        this.MQ_MEASUREMENT_UNIQUE_ID = m_uniqueid;
        this.MQ_REGISTRATION_UNIQUE_ID = r_uniqueid;
        this.MQ_HOST = hostname;
        this.MQ_PORT = portnumber;
        this.MQ_VHOST = vhost;
        this.MQ_EXCHANGE_NAME = exchangename;
        this.MQ_QUEUE_NAME = queuename;
        this.MQ_USERNAME = username;
        this.MQ_USERPASSWORD = password;
        this.messageStatus = messageStatus;
        this.alertSystemCache = alertSystemCache;

        log.debug("Host    :" + MQ_HOST);
        log.debug("Port    :" + MQ_PORT);
        log.debug("VHost   :" + MQ_VHOST);
        log.debug("ExName  :" + MQ_EXCHANGE_NAME);
        log.debug("QName   :" + MQ_QUEUE_NAME);
        log.debug("MeasID  :" + MQ_MEASUREMENT_UNIQUE_ID);
        log.debug("RegID   :" + MQ_REGISTRATION_UNIQUE_ID);

    }

    @Override
    public void sendMeasurement(SensorRecord sensorRecord) throws MessagingException {
        log.info("MQ Sending measurement message");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(MQ_HOST);
        factory.setPort(MQ_PORT);
        factory.setUsername(MQ_USERNAME);
        factory.setPassword(MQ_USERPASSWORD);
        factory.setVirtualHost(MQ_VHOST);

        try (Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {
            try {
                channel.queueDeclarePassive(MQ_QUEUE_NAME);
            } catch (IOException ex) {
                String errorMessage = "ERROR: Failed to connect to RabbitMQ Channel/Queue : " + MQ_QUEUE_NAME;
                messageSendFailed(errorMessage, sensorRecord);
                //todo Throw an error
                return;
            }
            log.debug("Connected to MQ Server");

            String messagesToEmit="";
            try {
                messagesToEmit = convertToJSONMesssage(sensorRecord);
            } catch (JsonProcessingException ex) {
                String errorMessage = "Converting SensorRecord to JSON failed : " + ex.getMessage();
                messageSendFailed(errorMessage, sensorRecord);
            }
            try {
                channel.basicPublish(MQ_EXCHANGE_NAME, MQ_MEASUREMENT_UNIQUE_ID, null, messagesToEmit.getBytes(StandardCharsets.UTF_8));
                log.info("MQ Sent message");
                log.debug("MQ Message : " + messagesToEmit);
                messageStatus.update(MessageStatusType.GOOD);
            } catch (IOException ex) {
                String errorMessage = "Failed to publish message to RabbitMQ Exchange :" + MQ_EXCHANGE_NAME;
                messageSendFailed(errorMessage, sensorRecord);
            }

        } catch (Exception ex) {
            String errorMessage = "Failed to connect to RabbitMQ host : " + MQ_HOST + " on port " + MQ_PORT;
            messageSendFailed(errorMessage, sensorRecord);
        }
    }

    private String convertToJSONMesssage(SensorRecord sensorRecord) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper.writeValueAsString(sensorRecord);
    }

    private void messageSendFailed(String message, SensorRecord sensorRecord) throws MessagingException {
        log.error(message);
        messageStatus.update(MessageStatusType.ERROR);
        alertSystemCache.add(message);
        alertService.sendAlert(sensorRecord,message);
    }
}
