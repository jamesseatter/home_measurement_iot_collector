package eu.seatter.homemeasurement.collector.actuator;

import eu.seatter.homemeasurement.collector.cache.MeasurementCacheImpl;
import eu.seatter.homemeasurement.collector.model.SensorMeasurementUnit;
import eu.seatter.homemeasurement.collector.model.SensorRecord;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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

    @Autowired
    MeasurementCacheImpl cache;

    @ReadOperation
    public Map<String, List<PublicMeasurement>>  measurements() {

        Map<String, List<SensorRecord>> data = cache.getAll();
        Map<String, List<PublicMeasurement>> dataout = new HashMap<>();

        for(String sensorid : data.keySet()) {
            dataout.putIfAbsent(sensorid,new ArrayList<PublicMeasurement>());
            for(SensorRecord sr : data.get(sensorid)) {
                PublicMeasurement pm = new PublicMeasurement();
                pm.setMeasureTimeUTC(sr.getMeasureTimeUTC());
                pm.setMeasurementUnit(sr.getMeasurementUnit());
                pm.setValue(sr.getValue());
                dataout.get(sensorid).add(pm);
            }
        }

        return dataout;
    }

//    @ReadOperation
//    public List<SensorRecord> measurements(@Selector String sensorid) {
//        return cache.getAllBySensorId(sensorid);
//    }
//

    @Getter
    @Setter
    private class PublicMeasurement {
        LocalDateTime measureTimeUTC;
        Double value;
        SensorMeasurementUnit measurementUnit;

    }

}
