package eu.seatter.homemeasurement.collector.services.messaging.azure;

import com.microsoft.azure.sdk.iot.device.*;
import com.microsoft.azure.sdk.iot.device.transport.IotHubConnectionStatus;
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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;

@Slf4j
@Service
//@Scope("singleton")
public class AzureIOTHub implements SensorMessaging {
    @Value("${spring.profiles.active:}")
    private String activeProfile;

    @Value("#{new Boolean('${azure.enabled}:false}')}")
    private boolean azureEnable;

    // Using the MQTT protocol to connect to IoT Hub
    private static final IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
    private DeviceClient client;

    private final Converter converter;
    private final MessageStatus messageStatus;
    private final AlertSystemCache alertSystemCache;

    private final String connectionString;

    public AzureIOTHub(Converter converter,
                       MessageStatus messageStatus,
                       AlertSystemCache alertSystemCache,
                       @Value("${azure.iothub.connectionstring:}") String connectionString
                       ) throws URISyntaxException, IOException {
        this.converter = converter;
        this.messageStatus = messageStatus;
        this.alertSystemCache = alertSystemCache;
        this.connectionString = connectionString;

        if (azureEnable) {
            // Connect to the IoT hub.
            client = new DeviceClient(this.connectionString, protocol);

            client.registerConnectionStatusChangeCallback((status, statusChangeReason, throwable, callbackContext) -> {
                log.warn("CONNECTION STATUS UPDATE: " + status);
                log.warn("CONNECTION STATUS REASON: " + statusChangeReason);
                log.warn("CONNECTION STATUS THROWABLE: " + (throwable == null ? "null" : throwable.getMessage()));

                if (status == IotHubConnectionStatus.DISCONNECTED) {
                    //connection was lost, and is not being re-established. Look at provided exception for
                    // how to resolve this issue. Cannot send messages until this issue is resolved, and you manually
                    // re-open the device client
                    try {
                        client.open();
                    } catch (IOException ex) {
                        log.error("Unable to re-open MQTT connection: " + ex.getMessage());
                    }
                } else if (status == IotHubConnectionStatus.DISCONNECTED_RETRYING) {
                    //connection was lost, but is being re-established. Can still send messages, but they won't
                    // be sent until the connection is re-established
                    log.warn("Retrying MQTT connection");
                } else if (status == IotHubConnectionStatus.CONNECTED) {
                    //Connection was successfully re-established. Can send messages.
                    log.info("Reconnected to MQTT");
                }
            }, new Object());

            client.open();
        }
    }

    public void reconnect() {
        log.warn("Azure reconnect initiated due to connection issues");
        try {
            client.closeNow();
            client = new DeviceClient(connectionString, protocol);
        } catch (IOException ex) {
            log.error("Exception closing Azure Client connection: " + ex.getMessage());
        } catch (URISyntaxException ex) {
            log.error("Exception creating an Azure Client connection: " + ex.getMessage());
        }
    }

    // Print the acknowledgement received from IoT Hub for the telemetry message sent.
    private static class EventCallback implements IotHubEventCallback {
        final java.util.UUID uuid;

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
        if(!azureEnable) return false;

        int retryCounter = 1;
        final int maxRetries = 3;
        while (retryCounter < maxRetries) {
            try {
                client.open();
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
                retryCounter++;
                reconnect();
            }
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
        alertSystemCache.add("Azure IOT Hub",message);
    }
}