package eu.seatter.homemeasurement.collector.services;

import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.model.MeasurementWeb;
import eu.seatter.homemeasurement.collector.services.sensor.SensorListService;
import eu.seatter.homemeasurement.collector.services.sensor.SensorMeasurement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 08/02/2021
 * Time: 22:09
 */
@Service
@Slf4j
public class MeasureNow {

    private SensorListService sensorListService;
    private SensorMeasurement sensorMeasurement;

    public MeasureNow(SensorListService sensorListService, SensorMeasurement sensorMeasurement) {
        this.sensorListService = sensorListService;
        this.sensorMeasurement = sensorMeasurement;
    }

    public List<MeasurementWeb> collect() {
        List<Measurement> sensorList = sensorListService.getSensors();

        sensorList = sensorMeasurement.collect(sensorList);

        List<MeasurementWeb> measurementWeb = new ArrayList<>();
        for (int i = 0; i <sensorList.size() ; i++) {
            Measurement m = sensorList.get(i);
            measurementWeb.add(MeasurementWeb.builder()
                    .sensorid(m.getSensorid())
                    .title(m.getTitle())
                    .shorttitle(m.getShortTitle())
                    .description(m.getDescription())
                    .measureTimeUTC(m.getMeasureTimeUTC())
                    .measurementUnit(m.getMeasurementUnit())
                    .value(m.getValue())
                    .build()
            );
        }


        return measurementWeb;
    }
}
