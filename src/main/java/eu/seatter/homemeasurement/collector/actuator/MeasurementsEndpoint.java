package eu.seatter.homemeasurement.collector.actuator;


import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.services.cache.AlertMeasurementCacheService;
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

    final AlertMeasurementCacheService cacheService;

    public MeasurementsEndpoint(AlertMeasurementCacheService cacheService) {
        this.cacheService = cacheService;
    }

    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @ReadOperation
    public Map<String, List<String>> measurements() {

        Map<String, List<Measurement>> data = cacheService.getAll();
        Map<String, List<String>> dataout = new HashMap<>();

//        for(String sensorid : data.keySet()) {
//            dataout.putIfAbsent(sensorid, new ArrayList<>());
//            for(Measurement sr : data.get(sensorid)) {
//                dataout.get(sensorid).add(formatMeasurements(sr));
//            }
//        }

        for (Map.Entry<String, List<Measurement>> entry : data.entrySet()) {
            dataout.putIfAbsent(entry.getKey(), new ArrayList<>());
            for(Measurement sr : entry.getValue()) {
                dataout.get(entry.getKey()).add(formatMeasurements(sr));
            }
        }

        return dataout;
    }

    private String formatMeasurements(Measurement record) {

        return record.getMeasureTimeUTC().format(formatter) +
                " UTC;" +
                record.getValue() +
                record.getMeasurementUnit();
    }

}
