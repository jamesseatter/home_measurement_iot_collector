package eu.seatter.homemeasurement.collector.services.cache;

import eu.seatter.homemeasurement.collector.cache.map.MeasurementCacheMapImpl;
import eu.seatter.homemeasurement.collector.model.Sensor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 20/05/2019
 * Time: 17:55
 */
@Service
public class MeasurementCacheService {
    private final MeasurementCacheMapImpl measurementCache;

    public MeasurementCacheService(MeasurementCacheMapImpl measurementCache) {
        this.measurementCache = measurementCache;
    }

    public void add(Sensor measurement) {
        measurementCache.add(measurement, false);
    }

    public Map<String, List<Sensor>> getAll() {
        return measurementCache.getAll();
    }

    public Map<String, List<Sensor>> getAllSorted() {
        return measurementCache.getAllSorted();
    }

    public boolean flushToFile()  throws IOException {
        return measurementCache.flushToFile();
    }

    public int readFromFile() throws IOException {
        return measurementCache.readFromFile();
    }
}
