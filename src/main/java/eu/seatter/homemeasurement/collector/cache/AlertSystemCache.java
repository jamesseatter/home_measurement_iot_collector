package eu.seatter.homemeasurement.collector.cache;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 14/02/2020
 * Time: 13:44
 */
public interface AlertSystemCache {
    void add(String alertMessage);
    List getAll();
    List getAllSorted();

    int getCacheMaxSize();
    int getCacheSize();
}
