package eu.seatter.homemeasurement.collector.cache;

import com.fasterxml.jackson.databind.JsonMappingException;
import eu.seatter.homemeasurement.collector.model.Measurement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 01/05/2019
 * Time: 16:01
 */
public interface MeasurementCache {
    void add(Measurement measurement);
    Map<String, List<Measurement>> getAll();
    Map<String,List<Measurement>> getAllSorted();
    List<Measurement> getAllBySensorId(String sensorId);
    //    List<measurement> getLastBySensorId(String sensorId);
    List<Measurement> getLastBySensorId(String sensorId, int last);

    ArrayList<String> getSensorIds();
    int getCacheMaxSizePerSensor();
    int getCacheSizeBySensorId(String sensorId);

    boolean flushToFile() throws JsonMappingException, IOException;
    int readFromFile() throws IOException;
}
