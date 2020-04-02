package eu.seatter.homemeasurement.collector.cache.map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.seatter.homemeasurement.collector.cache.MQCache;
import eu.seatter.homemeasurement.collector.model.Measurement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 25/03/2020
 * Time: 12:55
 */
@SuppressWarnings("DuplicatedCode")
@Slf4j
@Component
@Scope("singleton")
public class MQMeasurementCacheMapImpl implements MQCache {
    private List<Measurement> cache = new ArrayList<>();

    private final String CACHE_PATH;

    public MQMeasurementCacheMapImpl(@Value("${cache.mq.measurement.path}") String cachepath) {
        this.CACHE_PATH = cachepath;
    }

    @Override
    public int add(Measurement measurement) {
        if(!cache.contains(measurement)) {
            cache.add(measurement);
            try {
                flushToFile();
            } catch (IOException e) {
                //TODO Update error handling
                e.printStackTrace();
            }
        }
        return getCacheSize();
    }

    @Override
    public int remove(Measurement measurement) {
        if(cache.contains(measurement)) {
            cache.remove(measurement);
            try {
                flushToFile();
            } catch (IOException e) {
                //TODO Update error handling
                e.printStackTrace();
            }
        }
        return getCacheSize();
    }

    public int removeAll(List<Measurement> measurement) {
        cache.removeAll(measurement);
        try {
            flushToFile();
        } catch (IOException e) {
            //TODO Update error handling
            e.printStackTrace();
        }
        return getCacheSize();
    }

    @Override
    public List<Measurement> getAll() {
        return cache;
    }

    @Override
    public int getCacheSize() {
        return cache.size();
    }

    @Override
    public boolean flushToFile() throws IOException {
        File file = new File(CACHE_PATH);
        try {
            if(!Files.exists(Paths.get(CACHE_PATH))) {
                if (!file.getParentFile().mkdirs()) {
                    throw new FileSystemException("Unable to create folder file.getParentFile()");
                }
            }
        } catch(SecurityException ex) {
            throw new SecurityException("Unable to create folder due to security issues : " + ex.getMessage());
        }

        ObjectMapper mapper = new ObjectMapper();
        String jsonArray = mapper.writeValueAsString(cache);

        //write to file
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(jsonArray);
            fileWriter.flush();
            fileWriter.close();
            return true;
        } catch (IOException ex) {
            log.error("Unable to write to file : " + ex.getMessage());
            throw new IOException(ex);
        }
    }

    @Override
    public int readFromFile() throws IOException {
        String content = "";
        if(!Files.exists(Paths.get(CACHE_PATH))) {
            throw new FileNotFoundException("The file " + CACHE_PATH + " was not found");
        }
        try {
            content = new String(Files.readAllBytes(Paths.get(CACHE_PATH)));
        } catch (IOException ex) {
            log.error("Unable to read from file : " + ex.getMessage());
            throw new IOException(ex);
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        cache = mapper.readValue(content, new TypeReference<List<Measurement>>() { });
        return cache.size();
    }
}
