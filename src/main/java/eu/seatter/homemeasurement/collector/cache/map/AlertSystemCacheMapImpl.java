package eu.seatter.homemeasurement.collector.cache.map;

import com.fasterxml.jackson.databind.JsonMappingException;
import eu.seatter.homemeasurement.collector.cache.AlertSystemCache;
import eu.seatter.homemeasurement.collector.model.SystemAlert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
    private final String CACHE_PATH;

    public AlertSystemCacheMapImpl(@Value("${cache.mq.measurement.path}") String cachepath,
                                   @Value("${system.alert.cache.max_records_per_sensor:100}") int maxentriespersensor) {
        this.CACHE_PATH = cachepath;
        this.MAX_ENTRIES_PER_SENSOR = maxentriespersensor;
    }

    @Override
    public void add(String alertMessage) {
        SystemAlert sa = new SystemAlert().toBuilder()
                                            .time(ZonedDateTime.now(ZoneId.of("Etc/UTC")).truncatedTo(ChronoUnit.MINUTES))
                                            .alertMessage(alertMessage).build();

        if(cache.size() == MAX_ENTRIES_PER_SENSOR) {
            cache.remove(-1);
        }
        cache.add(0,sa);
        log.debug("System alert cache Add : " + alertMessage);
    }

    @Override
    public List getAll() {
        return cache;
    }

    @Override
    public List getAllSorted() {
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
    public boolean flushToFile() throws JsonMappingException, IOException {
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
    public int readFromFile() throws IOException {
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
