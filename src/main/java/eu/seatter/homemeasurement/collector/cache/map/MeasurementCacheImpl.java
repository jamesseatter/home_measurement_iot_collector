package eu.seatter.homemeasurement.collector.cache.map;

import eu.seatter.homemeasurement.collector.cache.MeasurementCache;
import eu.seatter.homemeasurement.collector.model.SensorRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
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
@Scope("singleton")
public class MeasurementCacheImpl implements MeasurementCache {
    private final Map<String,List<SensorRecord>> cache = new HashMap<>();

    @Value("${measurement.cache.max_records_per_sensor:24}")
    private final int MAX_ENTRIES_PER_SENSOR=24;

    public MeasurementCacheImpl() {}

    @Override
    public void add(SensorRecord sensorRecord) {
        SensorRecord toCache = sensorRecord.toBuilder().build();
        System.out.println(sensorRecord.hashCode() + "   /   " + toCache.hashCode());

        if(!cache.containsKey(toCache.getSensorid())) {
            // initialize new map entry for sensor
            cache.put(toCache.getSensorid(),new ArrayList<>(MAX_ENTRIES_PER_SENSOR));
        }

        if(cache.get(toCache.getSensorid()).size() == MAX_ENTRIES_PER_SENSOR) {
            cache.get(toCache.getSensorid()).remove(0);
        }
        //cache.get(toCache.getSensorid()).add(toCache);
        cache.get(toCache.getSensorid()).add(0,toCache);
        log.debug("Cache Add : " + toCache.toString());
    }

    @Override
    public Map<String, List<SensorRecord>> getAll() {
        return cache;
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
    public List<SensorRecord> getAllSorted() {
        List<SensorRecord> sortedRecords = new ArrayList<>();
        for(List<SensorRecord> srList : cache.values()) {
            for(SensorRecord sr : srList) {
                sortedRecords.add(sr);
            }
        }

        sortedRecords.sort(Comparator.comparing(SensorRecord::getMeasureTimeUTC)
                .thenComparing(SensorRecord::getSensorid));

        return sortedRecords;

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

//    @Override
//    public List<SensorRecord> getLastBySensorId(String sensorId) {
//        return getLastBySensorId(sensorId,1);
//    }

    @Override
    public ArrayList getSensorIds() {
        return new ArrayList<>(cache.keySet());
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
