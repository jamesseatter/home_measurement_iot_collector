package eu.seatter.homemeasurement.collector.actuator;


import eu.seatter.homemeasurement.collector.model.SensorRecord;
import eu.seatter.homemeasurement.collector.services.cache.CacheService;
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

    CacheService cacheService;

    public MeasurementsEndpoint(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @ReadOperation
    public Map<String, List<String>> measurements() {

        Map<String, List<SensorRecord>> data = cacheService.getAll();
        Map<String, List<String>> dataout = new HashMap<>();



        for(String sensorid : data.keySet()) {
            dataout.putIfAbsent(sensorid, new ArrayList<>());
            for(SensorRecord sr : data.get(sensorid)) {
                dataout.get(sensorid).add(formatMeasurements(sr));
            }
        }

        return dataout;
    }

//    @ReadOperation
//    public List<String> measurements(@Selector String sensorid) {
//        List<SensorRecord> data = cache.getAllBySensorId(sensorid);
//        List<String> dataout = new ArrayList<>();
//
//        for(SensorRecord sr : data) {
//            dataout.add(formatMeasurements(sr));
//        }
//        return dataout;
//    }


    private String formatMeasurements(SensorRecord record) {

        StringBuilder sb = new StringBuilder();
        sb.append(record.getMeasureTimeUTC().format(formatter));
        sb.append(" UTC;");
        sb.append(record.getValue());
        sb.append(record.getMeasurementUnit());

        return sb.toString();
    }

}
