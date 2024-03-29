package eu.seatter.homemeasurement.collector;

import eu.seatter.homemeasurement.collector.model.Sensor;
import eu.seatter.homemeasurement.collector.model.enums.MeasurementUnit;
import eu.seatter.homemeasurement.collector.model.enums.SensorType;
import eu.seatter.homemeasurement.collector.model.SystemAlert;
import eu.seatter.homemeasurement.collector.utils.UtilDateTime;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 02/02/2021
 * Time: 09:36
 */
public class TestData {
    public List<Sensor> getTestSensorList(){
        List<Sensor> sensorList = new ArrayList<>();
        sensorList.add(Sensor.builder()
                .recordUID(UUID.randomUUID())
                .sensorid("28-000008d2fdb9")
                .title("Température de l'eau à l'arrivée")
                .description("Returns the temperature of the hot water entering the house from the central heating system")
                .familyid(40)
                .sensortype(SensorType.ONEWIRE)
                .low_threshold(45.0)
                .high_threshold(75.0)
                .alertgroup("temperature_threshold_alerts_private")
                .alertdestination("BORRY")
                .build());

        sensorList.add(Sensor.builder()
                .recordUID(UUID.randomUUID())
                .sensorid("28-0000095fcafb")
                .title("Température de l'eau de chaudière")
                .description("Returns the temperature of the hot water in the boiler")
                .familyid(40)
                .sensortype(SensorType.ONEWIRE)
                .low_threshold(35.0)
                .high_threshold(60.0)
                .alertgroup("temperature_threshold_alerts_private")
                .alertdestination("PRIVATE")
                .build());

        return sensorList;
    }

    public List<Sensor> getTestMeasurements(List<Sensor> sensorList) {
        for(Sensor srec : sensorList) {
            srec.setMeasurementUnit(MeasurementUnit.C);
            srec.setMeasureTimeUTC(UtilDateTime.getTimeDateNowNoSecondsInUTC());
            int val = ThreadLocalRandom.current().nextInt(35, 75);
            srec.setValue((double)val);
        }
        return Collections.unmodifiableList(sensorList);
    }

    public Sensor getTestMeasurement(String sensorID, LocalDateTime localDateTime) {
        return Sensor.builder()
                .recordUID(UUID.randomUUID())
                .sensorid(sensorID)
                .title("Température de l'eau à l'arrivée")
                .description("Returns the temperature of the hot water entering the house from the central heating system")
                .familyid(40)
                .sensortype(SensorType.ONEWIRE)
                .low_threshold(45.0)
                .high_threshold(60.0)
                .alertgroup("temperature_threshold_alerts_private")
                .alertdestination("BORRY")
                .measureTimeUTC(localDateTime)
                .build();
    }

    public SystemAlert getTestSystemAlert(String title, String alertMessage) {
        return new SystemAlert().toBuilder()
                .alertTimeUTC(UtilDateTime.getTimeDateNowNoSecondsInUTC())
                .title("")
                .message(alertMessage)
                .build();
    }
}
