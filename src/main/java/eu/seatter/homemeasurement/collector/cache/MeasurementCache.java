package eu.seatter.homemeasurement.collector.cache;

import eu.seatter.homemeasurement.collector.model.SensorRecord;

import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 01/05/2019
 * Time: 16:01
 */
interface MeasurementCache {
    void add(SensorRecord sensorRecord);
    List<SensorRecord> getAllBySensorId(String sensorId);
    List<SensorRecord> getLastBySensorId(String sensorId);
    List<SensorRecord> getLastBySensorId(String sensorId, int last);

    Set<String> getSensorIds();
    int getCacheMaxSizePerSensor();
    int getCacheSizeBySensorId(String sensorId);
}
