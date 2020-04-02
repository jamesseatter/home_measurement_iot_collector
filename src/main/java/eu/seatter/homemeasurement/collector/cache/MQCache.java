package eu.seatter.homemeasurement.collector.cache;

import com.fasterxml.jackson.databind.JsonMappingException;
import eu.seatter.homemeasurement.collector.model.Measurement;

import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 25/03/2020
 * Time: 12:53
 */
public interface MQCache {
        int add(Measurement measurement);
        int remove(Measurement measurement);
        int removeAll(List<Measurement> measurement);
        List<Measurement> getAll();
        int getCacheSize();
        boolean flushToFile() throws JsonMappingException, IOException ;
        int readFromFile() throws IOException;
}
