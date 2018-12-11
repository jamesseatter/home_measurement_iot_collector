package eu.seatter.homeheating.collector.Services;

import eu.seatter.homeheating.collector.Domain.SensorRecord;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 08/12/2018
 * Time: 15:15
 */
public interface SensorService {
    SensorRecord readSensorData(SensorRecord sensorRecord);
}
