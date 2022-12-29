package eu.seatter.homemeasurement.collector.cache;

import eu.seatter.homemeasurement.collector.model.Sensor;

import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 25/03/2020
 * Time: 12:53
 */
public interface MQCache {
        int add(Sensor measurement);
        int remove(Sensor measurement);
        int removeAll(List<Sensor> measurement);
        List<Sensor> getAll();
        int getCacheSize();
        boolean flushToFile() throws IOException ;
        int readFromFile() throws IOException;
}
