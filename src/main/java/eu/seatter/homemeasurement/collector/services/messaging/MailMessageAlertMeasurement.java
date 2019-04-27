package eu.seatter.homemeasurement.collector.services.messaging;

import eu.seatter.homemeasurement.collector.model.SensorRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 12/04/2019
 * Time: 13:04
 */
@Slf4j
public class MailMessageAlertMeasurement implements MailMessage {
    private final SensorRecord sensorRecord;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    public MailMessageAlertMeasurement(SensorRecord sensorRecord) {
        this.sensorRecord = sensorRecord;
    }

    public String getSubject() {
        return "Home Monitor Alert - " + sensorRecord.getSensorid() + " - " + sensorRecord.getValue();
    }

    public String getContent() {

        return "<html>" +
                "<body>" +
                "<p>Home Monitor Alert : " + activeProfile + "</p>" +
                "<p>Sensor : " + sensorRecord.getSensorid() +
                "<p>TimeUTC : " + sensorRecord.getMeasureTimeUTC() +
                "<p>Value : " + sensorRecord.getValue() + sensorRecord.getMeasurementUnit() +
                "</body>" +
                "</html>";
    }
}
