package eu.seatter.homemeasurement.collector.services.cache;

import eu.seatter.homemeasurement.collector.cache.map.AlertSystemCacheMapImpl;
import eu.seatter.homemeasurement.collector.model.SystemAlert;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 12/02/2020
 * Time: 16:14
 */
@Service
public class AlertSystemCacheService {
    private final AlertSystemCacheMapImpl alertCache;

    public AlertSystemCacheService(AlertSystemCacheMapImpl alertCache) {
        this.alertCache = alertCache;
    }

    public void add(String title, String alert) {
        alertCache.add(title,alert);
    }

    public List<SystemAlert> getAll() {
        return alertCache.getAll();
    }

    public int readFromFile() throws IOException {
        return alertCache.readFromFile();
    }

    public void flushToFile()  throws IOException {
        alertCache.flushToFile();
    }
}
