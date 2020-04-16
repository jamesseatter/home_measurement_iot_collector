package eu.seatter.homemeasurement.collector.cache.map;

import eu.seatter.homemeasurement.collector.cache.MeasurementCache;
import eu.seatter.homemeasurement.collector.model.Measurement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 01/05/2019
 * Time: 16:02
 */
@SuppressWarnings("DuplicatedCode")
@Slf4j
@Component
@Scope("singleton")
public class AlertMeasurementCacheMapImpl implements MeasurementCache {
    private final Map<String,List<Measurement>> cache = new LinkedHashMap <>();

    private final int MAX_ENTRIES_PER_SENSOR;
    private final File CACHE_FILE;

    public AlertMeasurementCacheMapImpl(@Value("${cache.root.path}") String cache_path,
                                        @Value("${cache.alert.measurement.file}")String cache_file,
                                        @Value("${measurement.alert.cache.max_records_per_sensor:100}") int maxentriespersensor) {
        this.MAX_ENTRIES_PER_SENSOR = maxentriespersensor;
        this.CACHE_FILE = new File(cache_path, cache_file);
    }

    @Override
    public void add(Measurement measurement) {
        Measurement toCache = measurement.toBuilder().build();
        System.out.println(measurement.hashCode() + "   /   " + toCache.hashCode());

        if(!cache.containsKey(toCache.getSensorid())) {
            // initialize new map entry for sensor
            cache.put(toCache.getSensorid(),new ArrayList<>(MAX_ENTRIES_PER_SENSOR));
        }

        if(cache.get(toCache.getSensorid()).size() == MAX_ENTRIES_PER_SENSOR) {
            cache.get(toCache.getSensorid()).remove(cache.get(toCache.getSensorid()).size()-1);
        }
        //cache.get(toCache.getSensorid()).add(toCache);
        cache.get(toCache.getSensorid()).add(0,toCache);
        log.debug("Alert cache Add : " + toCache.toString());
    }

    @Override
    public Map<String, List<Measurement>> getAll() {
        return cache;
    }

    @Override
    public Map<String,List<Measurement>> getAllSorted() {
        Map<String,List<Measurement>> cacheSorted = cache;
        for(String id : cacheSorted.keySet()) {
            cacheSorted.get(id).sort(Comparator.comparing(Measurement::getMeasureTimeUTC).reversed());
        }

        return cacheSorted;
    }

    @Override
    public List<Measurement> getAllBySensorId(String sensorId) {
        if(cache.containsKey(sensorId)) {
            return Collections.unmodifiableList(this.cache.get(sensorId));
        } else {
            return Collections.emptyList();
        }
    }



    @Override
    public List<Measurement> getLastBySensorId(String sensorId, int last) {
        if(cache.containsKey(sensorId)) {
            if(cache.get(sensorId).size() < last) {
                throw new IllegalArgumentException("The number of values requested, " + last + ", is greater then the number of records cached for the sensor " + cache.get(sensorId).size());
            }

            //return Collections.unmodifiableList(this.cache.get(sensorId).stream().limit(last).collect(Collectors.toList()));
            List<Measurement> temp = this.cache.get(sensorId);

            return Collections.unmodifiableList(temp.subList(temp.size() - last, temp.size()));
        } else {
            throw new IllegalArgumentException("The sensor, " + sensorId + " has no records in the cache");
        }
    }

//    @Override
//    public List<measurement> getLastBySensorId(String sensorId) {
//        return getLastBySensorId(sensorId,1);
//    }

    @Override
    public ArrayList<String> getSensorIds() {
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

    @Override
    public boolean flushToFile() {
//        ObjectMapper mapper = new ObjectMapper();
//        String jsonArray = mapper.writeValueAsString(cache);
//
//        //write to file
//        try (FileWriter file = new FileWriter(CACHE_PATH)) {
//            file.write(jsonArray);
//            file.flush();
//            return true;
//        } catch (IOException e) {
//            //TODO Update error handling
//            e.printStackTrace();
//        }
        return false;
    }

    @Override
    public int readFromFile() {
//        String content = "";
//        try
//        {
//            content = new String ( Files.readAllBytes( Paths.get(CACHE_PATH) ) );
//        }
//        catch (IOException e)
//        {
//            //TODO Update error handling
//            e.printStackTrace();
//        }
//
//        ObjectMapper mapper = new ObjectMapper();
//        cache = mapper.readValue(content, List.class);
        return 0;
    }
}
