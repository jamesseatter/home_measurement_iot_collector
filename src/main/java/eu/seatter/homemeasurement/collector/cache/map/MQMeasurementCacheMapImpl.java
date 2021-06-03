package eu.seatter.homemeasurement.collector.cache.map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final File cacheFile;

    public MQMeasurementCacheMapImpl(@Value("${cache.root.path}") String cachePath,
                                     @Value("${cache.mqfailed.measurement.file}") String cacheFile) {
        this.cacheFile = new File(cachePath, cacheFile);
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
        File directory = new File(cacheFile.getParent());
        log.debug("File = " + cacheFile);
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

        ObjectMapper mapper = new ObjectMapper();
        String jsonArray = mapper.writeValueAsString(cache);

        //write to file
        try (FileWriter fileWriter = new FileWriter(cacheFile)) {
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
        if(!Files.exists(Paths.get(cacheFile.getPath()))) {
            throw new FileNotFoundException("The file " + cacheFile + " was not found");
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            cache = mapper.readValue(new File(cacheFile.getPath()), new TypeReference<List<Measurement>>() { });
        } catch (IOException ex) {
            log.error("Unable to read from file : " + ex.getMessage());
            throw new IOException(ex);
        }

        return cache.size();
    }
}
