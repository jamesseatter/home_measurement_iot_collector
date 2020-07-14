package eu.seatter.homemeasurement.collector.cache.map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private Map<String,List<Measurement>> cache = new LinkedHashMap <>();

    private final int maxentriespersensor;
    private final File cachefile;

    public MeasurementCacheMapImpl(@Value("${cache.root.path}") String cachefile,
                                   @Value("${cache.measurement.file}")String cacheFile,
                                   @Value("${measurement.cache.max_records_per_sensor:24}") int maxentriespersensor) {
        this.maxentriespersensor = maxentriespersensor;
        this.cachefile = new File(cachefile, cacheFile);
    }

    @Override
    public void add(Measurement measurement, boolean noFlush) {
        Measurement toCache = measurement.toBuilder().build();

        if(!cache.containsKey(toCache.getSensorid())) {
            // initialize new map entry for sensor
            cache.put(toCache.getSensorid(),new ArrayList<>(maxentriespersensor));
        }

        if(cache.get(toCache.getSensorid()).size() == maxentriespersensor) {
            cache.get(toCache.getSensorid()).remove(cache.get(toCache.getSensorid()).size()-1);
        }
        cache.get(toCache.getSensorid()).add(0,toCache);
        if(!noFlush) {
            log.debug("Measurement cache Add : " + toCache.toString());
            try {
                flushToFile();
            } catch (IOException e) {
                //TODO Update error handling
                e.printStackTrace();
            }
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
            List<Measurement> temp = this.cache.get(sensorId);

            return Collections.unmodifiableList(temp.subList(temp.size() - last, temp.size()));
        } else {
            throw new java.lang.IllegalArgumentException("The sensor, " + sensorId + " has no records in the cache");
        }
    }

    @Override
    public ArrayList<String> getSensorIds() {
        return new ArrayList<>(cache.keySet());
    }

    @Override
    public int getCacheMaxSizePerSensor() {
        return this.maxentriespersensor;
    }

    @Override
    public int getCacheSizeBySensorId(String sensorId) {
        return this.cache.get(sensorId).size();
    }

    private List<Measurement> getListOfMeasurements() {
        List<Measurement> measurements = new ArrayList<>();
        for(Map.Entry<String, List<Measurement>> entry : cache.entrySet()) {
            measurements.addAll(entry.getValue());
        }
//        for(String key : cache.keySet()) {
//            measurements.addAll(cache.get(key).subList(0,cache.get(key).size()));
//        }

        return measurements;
    }

    @Override
    public boolean flushToFile() throws  IOException {
        File directory = new File(cachefile.getParent());
        log.debug("File = " + cachefile.toString());
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
        try (FileWriter fileWriter = new FileWriter(cachefile)) {
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
        if(!Files.exists(Paths.get(cachefile.getPath()))) {
            throw new FileNotFoundException("The file " + cachefile.toString() + " was not found");
        }

        ObjectMapper mapper = new ObjectMapper();
        List<Measurement> measurements;
        try {
            measurements = mapper.readValue(new File(cachefile.getPath()), new TypeReference<List<Measurement>>() { });
        } catch (IOException ex) {
            log.error("Unable to read from file : " + ex.getMessage());
            throw new IOException(ex);
        }

        for(Measurement measurement : measurements) {
            add(measurement, true);
        }
        return measurements.size();
    }
}
