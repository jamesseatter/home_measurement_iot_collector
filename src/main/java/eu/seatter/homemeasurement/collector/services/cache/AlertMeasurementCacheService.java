package eu.seatter.homemeasurement.collector.services.cache;

import eu.seatter.homemeasurement.collector.cache.map.AlertMeasurementCacheMapImpl;
import eu.seatter.homemeasurement.collector.model.Sensor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 12/02/2020
 * Time: 16:14
 */
@Service
public class AlertMeasurementCacheService {
    private final AlertMeasurementCacheMapImpl alertCache;

    public AlertMeasurementCacheService(AlertMeasurementCacheMapImpl alertCache) {
        this.alertCache = alertCache;
    }

    public void add(Sensor measurement) {
        alertCache.add(measurement);
    }

    public Map<String, List<Sensor>> getAll() {
        return alertCache.getAll();
    }

    public Map<String, List<Sensor>> getAllSorted() {
        return alertCache.getAllSorted();
    }

    public int readFromFile() throws IOException {
        return alertCache.readFromFile();
    }

    public void flushToFile()  throws IOException {
        alertCache.flushToFile();
    }
}
