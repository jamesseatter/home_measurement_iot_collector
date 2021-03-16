package eu.seatter.homemeasurement.collector.services.messaging.azure;

import com.microsoft.azure.sdk.iot.device.*;
import eu.seatter.homemeasurement.collector.cache.AlertSystemCache;
import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.model.MeasurementAlert;
import eu.seatter.homemeasurement.collector.model.SystemAlert;
import eu.seatter.homemeasurement.collector.services.messaging.Converter;
import eu.seatter.homemeasurement.collector.services.messaging.MessageStatus;
import eu.seatter.homemeasurement.collector.services.messaging.MessageStatusType;
import eu.seatter.homemeasurement.collector.services.messaging.SensorMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;

@Slf4j
@Service
@Scope("singleton")
public class AzureIOTHub implements SensorMessaging {
    @Value("${spring.profiles.active:}")
    private String activeProfile;

    // Using the MQTT protocol to connect to IoT Hub
    private static final IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
    private final DeviceClient client;

    private final Converter converter;
    private final MessageStatus messageStatus;
    private final AlertSystemCache alertSystemCache;

    public AzureIOTHub(Converter converter,
                       MessageStatus messageStatus,
                       AlertSystemCache alertSystemCache,
                       @Value("${azure.iothub.hostname}") String hostName,
                       @Value("${azure.iothub.deviceid}") String deviceId,
                       @Value("${azure.iothub.sharedaccessKey}") String sharedAccessKey
                       ) throws IOException, URISyntaxException {
        this.converter = converter;
        this.messageStatus = messageStatus;
        this.alertSystemCache = alertSystemCache;
        // Connect to the IoT hub.
        // The device connection string to authenticate the device with your IoT hub.
        // Using the Azure CLI:
        // az iot hub device-identity show-connection-string --hub-name {YourIoTHubName} --device-id MyJavaDevice --output table

        String sb = "HostName=" + hostName + ";" +
                "DeviceId=" + deviceId + ";" +
                "SharedAccessKey=" + sharedAccessKey;
        client = new DeviceClient(sb, protocol);
        client.open();
    }

    // Print the acknowledgement received from IoT Hub for the telemetry message sent.
    private static class EventCallback implements IotHubEventCallback {
        java.util.UUID uuid;

        public EventCallback(java.util.UUID uuid) {
            this.uuid = uuid;
        }

        public void execute(IotHubStatusCode status, Object context) {
            log.info("Azure IoT Hub responded to message {" + uuid.toString() + "}with status: " + status.name());
        }
    }

    @Override
    public boolean sendMeasurement(Measurement measurement) {
        return sendMessage(measurement, measurement.getRecordUID());
    }

    @Override
    public boolean sendMeasurementAlert(MeasurementAlert measurementAlert)  {
        return sendMessage(measurementAlert, measurementAlert.getAlertUID());
    }

    @Override
    public boolean sendSystemAlert(SystemAlert systemAlert) {
        return sendMessage(systemAlert, systemAlert.getAlertUID());
    }

    private boolean sendMessage(Object message, java.util.UUID uuid) {

        try {
            String messagesToEmit = converter.convertToJSONMessage(message);
            Message msg = new Message(messagesToEmit);

            log.info("Azure Sending message {" + uuid.toString() + "}: " + messagesToEmit);
            Object lockobj = new Object();

            msg.setProperty("environment", activeProfile);

            // Send the message.
            EventCallback callback = new EventCallback(uuid);
            client.sendEventAsync(msg, callback, lockobj);

            return true;
        } catch (IOException | AmqpException ex) {
            messageSendFailed(ex.getMessage());
        }
        return false;
    }

    @Override
    public boolean flushCache() {
        return false;
    }

    private void messageSendFailed(String message) {
        log.error(message);
        messageStatus.update(MessageStatusType.ERROR);
        alertSystemCache.add(message);
    }
}