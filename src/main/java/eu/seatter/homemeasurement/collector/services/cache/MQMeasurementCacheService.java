package eu.seatter.homemeasurement.collector.services.cache;

import eu.seatter.homemeasurement.collector.cache.map.MQMeasurementCacheMapImpl;
import eu.seatter.homemeasurement.collector.model.Sensor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 25/03/2020
 * Time: 13:26
 */
@Service
public class MQMeasurementCacheService {
    private final MQMeasurementCacheMapImpl mqcache;

    public MQMeasurementCacheService(MQMeasurementCacheMapImpl mqcache) {
        this.mqcache = mqcache;
    }

    public void add(Sensor measurement) {
        mqcache.add(measurement);
    }

    public List<Sensor> getAll() {
        return mqcache.getAll();
    }

    public int getCacheSize() {
        return mqcache.getCacheSize();
    }

    public int readFromFile() throws IOException {
        return mqcache.readFromFile();
    }

    public void flushToFile()  throws IOException {
        mqcache.flushToFile();
    }
}
