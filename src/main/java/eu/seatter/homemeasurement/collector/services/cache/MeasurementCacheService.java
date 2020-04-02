package eu.seatter.homemeasurement.collector.services.cache;

import com.fasterxml.jackson.databind.JsonMappingException;
import eu.seatter.homemeasurement.collector.cache.map.MeasurementCacheMapImpl;
import eu.seatter.homemeasurement.collector.model.Measurement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
public class MeasurementCacheService {
    @Autowired
    private MeasurementCacheMapImpl measurementCache;

    public void add(Measurement measurement) {
        measurementCache.add(measurement);
    }

//    public void measurementSentToMq(UUID recordId, Boolean status) {
//        measurementCache.measurementSentToMq(recordId, status);
//    }
//
//    public void alertSentToMq(UUID recordId, Boolean status) {
//        measurementCache.alertSentToMq(recordId, status);
//    }

    public Map<String, List<Measurement>> getAll() {
        return measurementCache.getAll();
    }

    public List<Measurement> getAllBySensorId(String sensorId) {
        return measurementCache.getAllBySensorId(sensorId);
    }

    public Map<String, List<Measurement>> getAllSorted() {
        return measurementCache.getAllSorted();
    }

    public List<Measurement> getLastBySensorId(String sensorId, int last) {
        return measurementCache.getLastBySensorId(sensorId,last);
    }

    public ArrayList<String> getSensorIds() {
        return new ArrayList<>(measurementCache.getSensorIds());
    }

    public int getCacheMaxSizePerSensor() {
        return measurementCache.getCacheMaxSizePerSensor();
    }

    public int getCacheSizeBySensorId(String sensorId) {
        return measurementCache.getCacheSizeBySensorId(sensorId);
    }

    public boolean flushToFile()  throws JsonMappingException, IOException {
        return measurementCache.flushToFile();
    }

    public int readFromFile() throws JsonMappingException, IOException {
        return measurementCache.readFromFile();
    }
}
