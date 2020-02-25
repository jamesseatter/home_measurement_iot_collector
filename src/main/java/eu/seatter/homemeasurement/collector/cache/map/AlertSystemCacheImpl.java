package eu.seatter.homemeasurement.collector.cache.map;

import eu.seatter.homemeasurement.collector.cache.AlertSystemCache;
import eu.seatter.homemeasurement.collector.model.SystemAlert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 14/02/2020
 * Time: 13:46
 */
@Slf4j
@Component
@Scope("singleton")
public class AlertSystemCacheImpl implements AlertSystemCache {
    private final List<SystemAlert> cache = new ArrayList<>();

    @Value("${system.alert.cache.max_records_per_sensor:100}")
    private final int MAX_ENTRIES=100;

    public AlertSystemCacheImpl() {}

    @Override
    public void add(String alertMessage) {
        SystemAlert sa = new SystemAlert().toBuilder()
                                            .time(ZonedDateTime.now(ZoneId.of("Etc/UTC")).withSecond(00))
                                            .alertMessage(alertMessage).build();

        if(cache.size() == MAX_ENTRIES) {
            cache.remove(-1);
        }
        cache.add(0,sa);
        log.debug("System alert cache Add : " + alertMessage);
    }

    @Override
    public List getAll() {
        return cache;
    }

    @Override
    public List getAllSorted() {
        return cache;
    }

    @Override
    public int getCacheMaxSize() {
        return MAX_ENTRIES;
    }

    @Override
    public int getCacheSize() {
        return cache.size();
    }
}
