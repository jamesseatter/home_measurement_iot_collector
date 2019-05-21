package eu.seatter.homemeasurement.collector.services.cache;

import eu.seatter.homemeasurement.collector.cache.map.MeasurementCacheImpl;
import eu.seatter.homemeasurement.collector.model.SensorRecord;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 20/05/2019
 * Time: 17:55
 */
@Service
public class CacheService {
    private final MeasurementCacheImpl measurementCache;

    public CacheService(MeasurementCacheImpl measurementCache) {
        this.measurementCache = measurementCache;
    }

    public void add(SensorRecord sensorRecord) {
        measurementCache.add(sensorRecord);
    }

    public Map<String, List<SensorRecord>> getAll() {
        return measurementCache.getAll();
    }

    public List<SensorRecord> getAllBySensorId(String sensorId) {
        return measurementCache.getAllBySensorId(sensorId);
    }

    public List<SensorRecord> getAllSorted() {
        return measurementCache.getAllSorted();
    }

    public List<SensorRecord> getLastBySensorId(String sensorId, int last) {
        return measurementCache.getLastBySensorId(sensorId,last);
    }

    public List<SensorRecord> getLastBySensorId(String sensorId) {
        return measurementCache.getLastBySensorId(sensorId,1);
    }

    public ArrayList getSensorIds() {
        return new ArrayList<String>(measurementCache.getSensorIds());
    }

    public int getCacheMaxSizePerSensor() {
        return measurementCache.getCacheMaxSizePerSensor();
    }

    public int getCacheSizeBySensorId(String sensorId) {
        return measurementCache.getCacheSizeBySensorId(sensorId);
    }
}