package eu.seatter.homemeasurement.collector.services.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import eu.seatter.homemeasurement.collector.model.SensorRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    private final String MQ_MEASUREMENT_UNIQUE_ID;
    private final String MQ_REGISTRATION_UNIQUE_ID;
    private final String MQ_HOST;
    private final int MQ_PORT;
    private final String MQ_VHOST;
    private final String MQ_EXCHANGE_NAME;
    private final String MQ_QUEUE_NAME;
    private final String MQ_USERNAME;
    private final String MQ_USERPASSWORD;


    public RabbitMQService(@Value("${RabbitMQService.measurement.unique_id:CHANGE_ME}") String m_uniqueid,
                           @Value("${RabbitMQService.registration.unique_id:CHANGE_ME}") String r_uniqueid,
                           @Value("${RabbitMQService.hostname:localhost}") String hostname,
                           @Value("${RabbitMQService.portnumber:5672}") int portnumber,
                           @Value("${RabbitMQService.vhost:/}") String vhost,
                           @Value("${RabbitMQService.exchangee:}") String exchangename,
                           @Value("${RabbitMQService.queue:}") String queuename,
                           @Value("${RabbitMQService.username:}") String username,
                           @Value("${RabbitMQService.password:}") String password) {
        this.MQ_MEASUREMENT_UNIQUE_ID = m_uniqueid;
        this.MQ_REGISTRATION_UNIQUE_ID = r_uniqueid;
        this.MQ_HOST = hostname;
        this.MQ_PORT = portnumber;
        this.MQ_VHOST = vhost;
        this.MQ_EXCHANGE_NAME = exchangename;
        this.MQ_QUEUE_NAME = queuename;
        this.MQ_USERNAME = username;
        this.MQ_USERPASSWORD = password;

        log.debug("Host    :" + MQ_HOST);
        log.debug("Port    :" + MQ_PORT);
        log.debug("VHost   :" + MQ_VHOST);
        log.debug("ExName  :" + MQ_EXCHANGE_NAME);
        log.debug("QName   :" + MQ_QUEUE_NAME);
        log.debug("MeasID  :" + MQ_MEASUREMENT_UNIQUE_ID);
        log.debug("RegID   :" + MQ_REGISTRATION_UNIQUE_ID);

    }

    @Override
    public void sendMeasurement(SensorRecord sensorRecord) {
        log.info("MQ Sending measurement message");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(MQ_HOST);
        factory.setPort(MQ_PORT);
        factory.setUsername(MQ_USERNAME);
        factory.setPassword(MQ_USERPASSWORD);
        factory.setVirtualHost(MQ_VHOST);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

//            channel.exchangeDeclare(MQ_EXCHANGE_NAME, "topic", true);
//            channel.queueDeclare(MQ_QUEUE_NAME, true, false, false, null);
//            channel.queueBind(MQ_QUEUE_NAME, MQ_EXCHANGE_NAME, MQ_MEASUREMENT_UNIQUE_ID);
            try {
                channel.queueDeclarePassive(MQ_QUEUE_NAME);
            }
            catch(IOException ex) {
                System.out.println("ERROR: Failed to connect to RabbitMQ : " + ex.getLocalizedMessage());
                System.out.println("ERROR: Host : " + MQ_HOST + " vHost : " + MQ_VHOST + " Q : " + MQ_QUEUE_NAME);
                //todo Throw an error
                return;
            }
            log.debug("Connected to MQ Server");


            try {
                String messagesToEmit = convertToJSONMesssage(sensorRecord);
                channel.basicPublish(MQ_EXCHANGE_NAME, MQ_MEASUREMENT_UNIQUE_ID, null, messagesToEmit.getBytes(StandardCharsets.UTF_8));
                log.info("MQ Sent message");
                log.debug("MQ Message : " + messagesToEmit);
            }
            catch (JsonProcessingException ex) {
                log.error("Converting SensorRecord to JSON failed : " + ex.getMessage());

            }

        } catch (Exception ex) {
            log.error("Failed to connect to RabbitMQ with error message :" + ex.getMessage());
        }
    }

    private String convertToJSONMesssage(SensorRecord sensorRecord) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(sensorRecord);

    }
}
