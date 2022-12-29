package eu.seatter.homemeasurement.collector.cache;

import eu.seatter.homemeasurement.collector.model.Sensor;

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
public interface AlertMeasurementCache {
    void add(Sensor measurement);
    Map<String, List<Sensor>> getAll();
    Map<String,List<Sensor>> getAllSorted();
    List<Sensor> getAllBySensorId(String sensorId);
    List<Sensor> getLastBySensorId(String sensorId, int last);


    ArrayList<String> getSensorIds();
    int getCacheMaxSizePerSensor();
    int getCacheSizeBySensorId(String sensorId);

    boolean flushToFile() throws IOException;
    int readFromFile() throws IOException;
}
