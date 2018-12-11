package eu.seatter.homeheating.collector.Application;

import eu.seatter.homeheating.collector.Domain.SensorRecord;
import eu.seatter.homeheating.collector.Sensor.OneWirePi4JSensor;
import eu.seatter.homeheating.collector.Sensor.Sensor;
import eu.seatter.homeheating.collector.Sensor.SensorFactory;
import eu.seatter.homeheating.collector.Services.SensorService;
import eu.seatter.homeheating.collector.Services.SensorServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 10/12/2018
 * Time: 10:19
 */
@Component
public class Collector {

    private SensorService sensorService;
    private OneWirePi4JSensor oneWirePi4JSensor;
    private Logger logger = LoggerFactory.getLogger(Collector.class);
    private List<SensorRecord> sensorList;
    private Sensor sensorReader;

    @Value( "${sensor.ReadIntervalMinutes}" )
    private Long readInterval;


    public Collector(SensorService sensorService, OneWirePi4JSensor oneWirePi4JSensor) {
        this.sensorService = sensorService;
        this.oneWirePi4JSensor = oneWirePi4JSensor;
    }

    public void execute() {

        sensorList = SensorListManager.LoadSensorJSON();
        logger.info("Loaded list of sensors. Count : " + sensorList.size());

        for(SensorRecord sensorRecord: sensorList) {
            sensorReader = SensorFactory.getSensor(sensorRecord.getSensorType());

            sensorService = new SensorServiceImpl(sensorReader);
            try {
                sensorRecord = sensorService.readSensorData(sensorRecord);
                logger.debug("Sensor Record Updated. " + sensorRecord);
            } catch (RuntimeException e) {
                logger.error(e.getMessage());
            }
        }
    }

}
