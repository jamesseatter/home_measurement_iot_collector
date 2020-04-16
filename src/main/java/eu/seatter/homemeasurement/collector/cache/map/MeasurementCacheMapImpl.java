package eu.seatter.homemeasurement.collector.cache.map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.seatter.homemeasurement.collector.cache.MeasurementCache;
import eu.seatter.homemeasurement.collector.model.Measurement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
public class MeasurementCacheMapImpl implements MeasurementCache {
    private final Map<String,List<Measurement>> cache = new LinkedHashMap <>();

    private final int MAX_ENTRIES_PER_SENSOR;
//    private final String CACHE_PATH;
//    private final String CACHE_FILENAME;
    private final File CACHE_FILE;

    public MeasurementCacheMapImpl(@Value("${cache.root.path}") String cache_path,
                                   @Value("${cache.measurement.file}")String cache_file,
                                   @Value("${measurement.cache.max_records_per_sensor:24}") int maxentriespersensor) {
//        this.CACHE_PATH = cache_path;
//        this.CACHE_FILENAME = cache_file;
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
        log.debug("Measurement cache Add : " + toCache.toString());
        try {
            flushToFile();
        } catch (IOException e) {
            //TODO Update error handling
            e.printStackTrace();
        }
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
            throw new java.lang.IllegalArgumentException("The sensor, " + sensorId + " has no records in the cache");
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

    private List<Measurement> getListOfMeasurements() {
        List<Measurement> measurements = new ArrayList<>();
        for(String key : cache.keySet()) {
            measurements.addAll(cache.get(key).subList(0,cache.get(key).size()));
        }

        return measurements;
    }

    @Override
    public boolean flushToFile() throws  IOException {
        //File file = new File(CACHE_FILE);
        File directory = new File(CACHE_FILE.getParent());
        log.debug("File = " + CACHE_FILE.toString());
        log.debug("Directory = " + directory.toString());
        try {
            if(!directory.exists()) {
                directory.mkdir();
            }
        } catch(SecurityException ex) {
            log.error("Security Exception, unable to create directory due to security issues : " + ex.getMessage());
            return false;
        } catch (Exception ex) {
            log.error("Exception detected : " + ex.getMessage());
            return false;
        }

        List<Measurement> measurements = getListOfMeasurements();

        ObjectMapper mapper = new ObjectMapper();
        String jsonArray = mapper.writeValueAsString(measurements);

        //write to file
        try (FileWriter fileWriter = new FileWriter(CACHE_FILE)) {
            fileWriter.write(jsonArray);
            fileWriter.flush();
            return true;
        } catch (IOException ex) {
            log.error("Unable to write to file : " + ex.getMessage());
            throw new IOException(ex);
        }
    }

    @Override
    public int readFromFile() throws IOException {
        String content = "";
        if(!Files.exists(Paths.get(CACHE_FILE.getPath()))) {
            throw new FileNotFoundException("The file " + CACHE_FILE.toString() + " was not found");
        }
        try {
            content = new String(Files.readAllBytes(Paths.get(CACHE_FILE.getPath())));
        } catch (IOException ex) {
            log.error("Unable to read from file : " + ex.getMessage());
            throw new IOException(ex);
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        List<Measurement> measurements = mapper.readValue(content, new TypeReference<List<Measurement>>() { });

        for(Measurement measurement : measurements) {
            add(measurement);
        }
        return measurements.size();
    }
}
