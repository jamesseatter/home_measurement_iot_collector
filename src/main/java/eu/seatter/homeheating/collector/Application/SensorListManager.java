package eu.seatter.homeheating.collector.Application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.seatter.homeheating.collector.Domain.SensorRecord;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 10/12/2018
 * Time: 12:44
 */
public class SensorListManager {
    private static String SENSORJSONFILE = "/sensors/sensorlist.json";


    static List<SensorRecord> LoadSensorJSON() {
        List<SensorRecord> sensorList = Collections.emptyList();
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        TypeReference<List<SensorRecord>> typeReference = new TypeReference<List<SensorRecord>>() {
        };
        InputStream inputStream = TypeReference.class.getResourceAsStream(SENSORJSONFILE);
        try {
            sensorList = mapper.readValue(inputStream, typeReference);

        } catch (IOException e) {
            System.out.println("Unable to save users: " + e.getMessage());
        }
        return sensorList;
    }
}
