package eu.seatter.homemeasurement.collector.services.cache;

import eu.seatter.homemeasurement.collector.cache.map.MQMeasurementCacheMapImpl;
import eu.seatter.homemeasurement.collector.model.Measurement;
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

    public void add(Measurement measurement) {
        mqcache.add(measurement);
    }

//    public void measurementSentToMq(UUID recordId, Boolean status) {
//        measurementCache.measurementSentToMq(recordId, status);
//    }
//
//    public void alertSentToMq(UUID recordId, Boolean status) {
//        measurementCache.alertSentToMq(recordId, status);
//    }

    public int remove(Measurement measurement) {
        return mqcache.remove(measurement);
    }

    public int removeAll(List<Measurement> measurements) {
        return mqcache.removeAll(measurements);
    }

    public List<Measurement> getAll() {
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
