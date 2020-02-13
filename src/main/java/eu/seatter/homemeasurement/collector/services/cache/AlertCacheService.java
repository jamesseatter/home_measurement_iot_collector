package eu.seatter.homemeasurement.collector.services.cache;

import eu.seatter.homemeasurement.collector.cache.map.AlertCacheImpl;
import eu.seatter.homemeasurement.collector.model.SensorRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 12/02/2020
 * Time: 16:14
 */
@Service
public class AlertCacheService {
    @Autowired
    private AlertCacheImpl alertCache;

    public void add(SensorRecord sensorRecord) {
        alertCache.add(sensorRecord);
    }

    public Map<String, List<SensorRecord>> getAll() {
        return alertCache.getAll();
    }

    public List<SensorRecord> getAllBySensorId(String sensorId) {
        return alertCache.getAllBySensorId(sensorId);
    }

    public Map<String, List<SensorRecord>> getAllSorted() {
        return alertCache.getAllSorted();
    }

    public List<SensorRecord> getLastBySensorId(String sensorId, int last) {
        return alertCache.getLastBySensorId(sensorId,last);
    }

    public ArrayList<String> getSensorIds() {
        return new ArrayList<>(alertCache.getSensorIds());
    }

    public int getCacheMaxSizePerSensor() {
        return alertCache.getCacheMaxSizePerSensor();
    }

    public int getCacheSizeBySensorId(String sensorId) {
        return alertCache.getCacheSizeBySensorId(sensorId);
    }
}
