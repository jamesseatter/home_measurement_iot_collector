package eu.seatter.homeheating.collector.Services;

import eu.seatter.homeheating.collector.Domain.DataRecord;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 08/12/2018
 * Time: 15:15
 */
public interface SensorService {
    DataRecord readSensorData(DataRecord sensorRecord);
}
