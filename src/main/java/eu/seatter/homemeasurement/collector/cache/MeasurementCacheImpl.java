package eu.seatter.homemeasurement.collector.cache;

import eu.seatter.homemeasurement.collector.model.SensorRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 01/05/2019
 * Time: 16:02
 */
@Slf4j
@Component
public class MeasurementCacheImpl implements MeasurementCache {
    private final Map<String,List<SensorRecord>> cache = new HashMap<>();

    private int MAX_ENTRIES_PER_SENSOR = 24;

    public MeasurementCacheImpl() {}

    public MeasurementCacheImpl(int MAX_ENTRIES_PER_SENSOR) {
        this.MAX_ENTRIES_PER_SENSOR = MAX_ENTRIES_PER_SENSOR;
    }

    @Override
    public void add(SensorRecord sensorRecord) {
        if(!cache.containsKey(sensorRecord.getSensorid())) {
            // initialize new Map entry for sensor
            cache.put(sensorRecord.getSensorid(),new ArrayList<>(MAX_ENTRIES_PER_SENSOR));
        }

        if(cache.get(sensorRecord.getSensorid()).size() == MAX_ENTRIES_PER_SENSOR) {
            cache.get(sensorRecord.getSensorid()).remove(0);
        }
        cache.get(sensorRecord.getSensorid()).add(sensorRecord);
        log.debug("Cache Add : " + sensorRecord.toString());
    }

    @Override
    public List<SensorRecord> getAllBySensorId(String sensorId) {
        if(cache.containsKey(sensorId)) {
            return Collections.unmodifiableList(this.cache.get(sensorId));
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<SensorRecord> getLastBySensorId(String sensorId, int last) {
        if(cache.containsKey(sensorId)) {
            if(cache.get(sensorId).size() < last) {
                throw new IllegalArgumentException("The number of values requested, " + last + ", is greater then the number of records cached for the sensor " + cache.get(sensorId).size());
            }

            //return Collections.unmodifiableList(this.cache.get(sensorId).stream().limit(last).collect(Collectors.toList()));
            List<SensorRecord> temp = this.cache.get(sensorId);

            return Collections.unmodifiableList(temp.subList(temp.size() - last, temp.size()));
        } else {
            throw new java.lang.IllegalArgumentException("The sensor, " + sensorId + " has no records in the cache");
        }
    }

    @Override
    public List<SensorRecord> getLastBySensorId(String sensorId) {
        return getLastBySensorId(sensorId,1);
    }

    @Override
    public Set<String> getSensorIds() {
        return cache.keySet();
    }

    @Override
    public int getCacheMaxSizePerSensor() {
        return this.MAX_ENTRIES_PER_SENSOR;
    }

    @Override
    public int getCacheSizeBySensorId(String sensorId) {
        return this.cache.get(sensorId).size();
    }
}
