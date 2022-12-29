package eu.seatter.homemeasurement.collector.cache;

import eu.seatter.homemeasurement.collector.model.Sensor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 01/05/2019
 * Time: 16:01
 */
public interface MeasurementCache {
    void add(Sensor measurement, boolean noFlush);
    Map<String, List<Sensor>> getAll();
    Map<String,List<Sensor>> getAllSorted();
    boolean flushToFile() throws IOException;
    int readFromFile() throws IOException;
}
