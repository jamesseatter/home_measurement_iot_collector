//package eu.seatter.homemeasurement.collector.cache.map;
//
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import eu.seatter.homemeasurement.collector.model.Measurement;
//import eu.seatter.homemeasurement.collector.model.SensorType;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//
///**
// * Created by IntelliJ IDEA.
// * User: jas
// * Date: 25/03/2020
// * Time: 16:44
// */
//public class MQMeasurementCacheMapImplTest {
//
//
//    @Test
//    public void flushToDisk() throws JsonMappingException, IOException {
//
//        ObjectMapper mapper = new ObjectMapper();
//
//        List<Measurement> listOfRecords = new ArrayList<>();
//        listOfRecords.add(Measurement.builder()
//                .sensorid("28-000000000001")
//                .title("Température de l'eau à l'arrivée")
//                .description("Returns the temperature of the hot water entering the house from the central heating system")
//                .familyid(40)
//                .sensorType(SensorType.ONEWIRE)
//                .low_threshold(45.0)
//                .high_threshold(60.0)
//                .alertgroup("temperature_threshold_alerts_private")
//                .alertdestination("BORRY")
//                .build());
//
//        listOfRecords.add(Measurement.builder()
//                .sensorid("28-000000000002")
//                .title("Température de l'eau de chaudière")
//                .description("Returns the temperature of the hot water in the boiler")
//                .familyid(40)
//                .sensorType(SensorType.ONEWIRE)
//                .low_threshold(35.0)
//                .high_threshold(60.0)
//                .alertgroup("temperature_threshold_alerts_private")
//                .alertdestination("PRIVATE")
//                .build());
//
//
//            String jsonArray = mapper.writeValueAsString(listOfRecords);
//
//            List<Measurement> asList = mapper.readValue(jsonArray, List.class);
//
//    }
//
//}