package eu.seatter.homemeasurement.collector.cache.map;

import eu.seatter.homemeasurement.collector.cache.AlertSystemCache;
import eu.seatter.homemeasurement.collector.model.SystemAlert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
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
    private final List<SystemAlert> cache = new ArrayList<>();

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
                                            .alertTimeUTC(ZonedDateTime.now(ZoneId.of("Etc/UTC")).truncatedTo(ChronoUnit.MINUTES))
                                            .title("")
                                            .message(alertMessage).build();

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
    public boolean flushToFile() {
//        ObjectMapper mapper = new ObjectMapper();
//        String jsonArray = mapper.writeValueAsString(cache);
//
//        //write to file
//        try (FileWriter file = new FileWriter(cache_path)) {
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
//            content = new String ( Files.readAllBytes( Paths.get(cache_path) ) );
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
