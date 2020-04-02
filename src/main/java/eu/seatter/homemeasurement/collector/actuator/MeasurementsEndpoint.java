package eu.seatter.homemeasurement.collector.actuator;


import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.services.cache.AlertCacheService;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 07/05/2019
 * Time: 14:02
 */
@Component
@Endpoint(id = "measurements")
public class MeasurementsEndpoint {

    final AlertCacheService cacheService;

    public MeasurementsEndpoint(AlertCacheService cacheService) {
        this.cacheService = cacheService;
    }

    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @ReadOperation
    public Map<String, List<String>> measurements() {

        Map<String, List<Measurement>> data = cacheService.getAll();
        Map<String, List<String>> dataout = new HashMap<>();



        for(String sensorid : data.keySet()) {
            dataout.putIfAbsent(sensorid, new ArrayList<>());
            for(Measurement sr : data.get(sensorid)) {
                dataout.get(sensorid).add(formatMeasurements(sr));
            }
        }

        return dataout;
    }

//    @ReadOperation
//    public List<String> measurements(@Selector String sensorid) {
//        List<measurement> data = cache.getAllBySensorId(sensorid);
//        List<String> dataout = new ArrayList<>();
//
//        for(measurement sr : data) {
//            dataout.add(formatMeasurements(sr));
//        }
//        return dataout;
//    }


    private String formatMeasurements(Measurement record) {

        StringBuilder sb = new StringBuilder();
        sb.append(record.getMeasureTimeUTC().format(formatter));
        sb.append(" UTC;");
        sb.append(record.getValue());
        sb.append(record.getMeasurementUnit());

        return sb.toString();
    }

}
