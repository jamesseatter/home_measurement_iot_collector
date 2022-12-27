package eu.seatter.homemeasurement.collector.cache.map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
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
import java.time.LocalDateTime;
import java.util.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

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
    private final int maxentriespersensor;
    private final int maxageofentriesindays;
    private final File cachefile;

    public MeasurementCacheMapImpl(@Value("${cache.root.path}") String cachefile,
                                   @Value("${cache.measurement.file}")String cacheFile,
                                   @Value("${measurement.cache.max_records_per_sensor:24}") int maxentriespersensor,
                                   @Value("${measurement.cache.max_age.days:14}") int maxageofentriesindays) {
        this.maxentriespersensor = maxentriespersensor;
        this.maxageofentriesindays = maxageofentriesindays;
        this.cachefile = new File(cachefile, cacheFile);
    }

    @Override
    public void add(Measurement measurement, boolean noFlush) {
        LocalDateTime curDate = LocalDateTime.now();

        Measurement toCache = measurement.toBuilder().build();

        if(toCache.getMeasureTimeUTC().isBefore(curDate.minusDays(maxageofentriesindays))) {
            //Ignore the entry in the cache file.
            return;
        }

        if(!cache.containsKey(toCache.getSensorid())) {
            // initialize new map entry for sensor
            cache.put(toCache.getSensorid(),new ArrayList<>(maxentriespersensor));
        }

        if(cache.get(toCache.getSensorid()).size() == maxentriespersensor) {
            cache.get(toCache.getSensorid()).remove(cache.get(toCache.getSensorid()).size()-1);
        }
        cache.get(toCache.getSensorid()).add(0,toCache);
        if(!noFlush) {
            log.debug("Measurement cache Add : " + toCache);
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
//        for(Map.Entry<String, List<Measurement>> id : cacheSorted.entrySet()) {
//            cacheSorted.get(id.getKey()).sort(Comparator.comparing(Measurement::getMeasureTimeUTC).reversed());
//        }
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
        return measurements;
    }

    @Override
    public boolean flushToFile() throws  IOException {
        File directory = new File(cachefile.getParent());
        log.debug("File = " + cachefile);
        log.debug("Directory = " + directory);
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

        JsonMapper mapper = JsonMapper.builder()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
                .serializationInclusion(NON_NULL)
                .build();
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
            throw new FileNotFoundException("The file " + cachefile + " was not found");
        }

        JsonMapper mapper = JsonMapper.builder()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
                .serializationInclusion(NON_NULL)
                .build();
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
        // Rewrite the cache file to remove entries that are too old.
        // This is hygiene in cases where code dies for a while.
        flushToFile();
        return measurements.size();
    }
}
