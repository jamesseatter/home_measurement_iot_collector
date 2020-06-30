package eu.seatter.homemeasurement.collector.cache.map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.seatter.homemeasurement.collector.cache.AlertSystemCache;
import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.model.SystemAlert;
import eu.seatter.homemeasurement.collector.utils.UtilDateTime;
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
 * Date: 14/02/2020
 * Time: 13:46
 */
@Slf4j
@Component
@Scope("singleton")
public class AlertSystemCacheMapImpl implements AlertSystemCache {
    private List<SystemAlert> cache = new ArrayList<>();

    private final int MAX_ENTRIES_PER_SENSOR;
    private final File CACHE_FILE;

    public AlertSystemCacheMapImpl(@Value("${cache.root.path}") String cache_path,
                                   @Value("${cache.alert.system.file}")String cache_file,
                                   @Value("${system.alert.cache.max_records_per_sensor:100}") int maxentriespersensor) {
        this.MAX_ENTRIES_PER_SENSOR = maxentriespersensor;
        this.CACHE_FILE = new File(cache_path, cache_file);
    }

    @Override
    public void add(String alertMessage) {
        SystemAlert sa = new SystemAlert().toBuilder()
                .alertTimeUTC(UtilDateTime.getTimeDateNowNoSecondsInUTC())
                .title("")
                .message(alertMessage)
                .build();

        if(cache.size() == MAX_ENTRIES_PER_SENSOR) {
            cache.remove(-1);
        }
        cache.add(0,sa);
        log.debug("System alert cache Add : " + alertMessage);
    }

    @Override
    public List<SystemAlert> getAll() {
        return cache;
    }

    @Override
    public List<SystemAlert> getAllSorted() {
        return cache;
    }

    @Override
    public int getCacheMaxSize() {
        return MAX_ENTRIES_PER_SENSOR;
    }

    @Override
    public int getCacheSize() {
        return cache.size();
    }

    @Override
    public boolean flushToFile() throws IOException {
        //File file = new File(CACHE_FILE);
        File directory = new File(CACHE_FILE.getParent());
        log.debug("File = " + CACHE_FILE.toString());
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
        try (FileWriter fileWriter = new FileWriter(CACHE_FILE)) {
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
        if(!Files.exists(Paths.get(CACHE_FILE.getPath()))) {
            throw new FileNotFoundException("The file " + CACHE_FILE.toString() + " was not found");
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            cache = mapper.readValue(new File(CACHE_FILE.getPath()), new TypeReference<List<Measurement>>() { });
        } catch (IOException ex) {
            log.error("Unable to read from file : " + ex.getMessage());
            throw new IOException(ex);
        }

        return cache.size();
    }
}
