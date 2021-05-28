package eu.seatter.homemeasurement.collector.cache.map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.seatter.homemeasurement.collector.cache.AlertSystemCache;
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

    private final int maxEntriesPerSensor;
    private final File cacheFile;

    public AlertSystemCacheMapImpl(@Value("${cache.root.path}") String cachePath,
                                   @Value("${cache.alert.system.file}")String cacheFile,
                                   @Value("${system.alert.cache.max_records_per_sensor:100}") int maxentriespersensor) {
        this.maxEntriesPerSensor = maxentriespersensor;
        this.cacheFile = new File(cachePath, cacheFile);
    }

    @Override
    public SystemAlert add(String title,String alertMessage) {
        SystemAlert sa = new SystemAlert().toBuilder()
                .alertTimeUTC(UtilDateTime.getTimeDateNowNoSecondsInUTC())
                .title(title)
                .message(alertMessage)
                .build();

        if(cache.size() == maxEntriesPerSensor) {
            cache.remove(cache.size() -1);
        }
        cache.add(0,sa);
        log.debug("System alert cache Add : " + alertMessage);
        return sa;
    }

    @Override
    public List<SystemAlert> getAll() {
        return cache;
    }

    @Override
    public List<SystemAlert> getAllSorted() {
        return getAll();
    }

    @Override
    public int getCacheMaxSize() {
        return maxEntriesPerSensor;
    }

    @Override
    public int getCacheSize() {
        return cache.size();
    }

    @Override
    public boolean flushToFile() throws IOException {
        File directory = new File(cacheFile.getParent());
        log.debug("File = " + cacheFile.toString());
        try {
            if(!directory.exists()) {
                boolean mkdir = directory.mkdir();
                if (!mkdir) {
                    log.error("Unable to create the file: " + directory.toString());
                    return false;
                }
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
            throw new FileNotFoundException("The file " + cacheFile.toString() + " was not found");
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            cache = mapper.readValue(new File(cacheFile.getPath()), new TypeReference<List<SystemAlert>>() { });
        } catch (IOException ex) {
            log.error("Unable to read from file : " + ex.getMessage());
            throw new IOException(ex);
        }

        return cache.size();
    }
}
