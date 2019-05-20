package eu.seatter.homemeasurement.collector.cache;

import eu.seatter.homemeasurement.collector.model.SensorRecord;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 01/05/2019
 * Time: 16:01
 */
public interface MeasurementCache {
    void add(SensorRecord sensorRecord);
    Map<String, List<SensorRecord>> getAll();
    List<SensorRecord> getAllBySensorId(String sensorId);
    List<SensorRecord> getLastBySensorId(String sensorId);
    List<SensorRecord> getLastBySensorId(String sensorId, int last);
    List<SensorRecord> getAllSorted();

    List<String> getSensorIds();
    int getCacheMaxSizePerSensor();
    int getCacheSizeBySensorId(String sensorId);
}
