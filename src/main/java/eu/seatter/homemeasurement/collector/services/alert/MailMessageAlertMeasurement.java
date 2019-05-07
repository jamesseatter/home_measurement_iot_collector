package eu.seatter.homemeasurement.collector.services.alert;

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

    public String getAddress() throws IllegalArgumentException {
        if(sensorRecord.getAlertgroup().isEmpty()) {
            return "";
        }
        String alertGroup = sensorRecord.getAlertgroup();
        AlertContactGroup ag = AlertContactJSON.GetContactsForGroup(alertGroup).orElse(new AlertContactGroup());
        if(ag.getAddress().isEmpty()) {
            throw new java.lang.IllegalArgumentException("No recipients found for alertgroup:" + alertGroup);
        }
        return ag.getAddress();
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
