package eu.seatter.homemeasurement.collector.cache;

import eu.seatter.homemeasurement.collector.model.SystemAlert;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 14/02/2020
 * Time: 13:44
 */
@SuppressWarnings("SameReturnValue")
public interface AlertSystemCache {
    void add(String alertMessage);
    List<SystemAlert> getAll();
    List<SystemAlert> getAllSorted();

    int getCacheMaxSize();
    int getCacheSize();

    boolean flushToFile();
    int readFromFile();
}
