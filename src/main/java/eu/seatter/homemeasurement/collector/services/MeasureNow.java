package eu.seatter.homemeasurement.collector.services;

import eu.seatter.homemeasurement.collector.converter.ConvertMeasurement;
import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.model.MeasurementWeb;
import eu.seatter.homemeasurement.collector.services.sensor.SensorListService;
import eu.seatter.homemeasurement.collector.services.sensor.SensorMeasurement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    private final SensorListService sensorListService;
    private final SensorMeasurement sensorMeasurement;

    private final ConvertMeasurement convertMeasurement;

    public MeasureNow(SensorListService sensorListService, SensorMeasurement sensorMeasurement, ConvertMeasurement convertMeasurement) {
        this.sensorListService = sensorListService;
        this.sensorMeasurement = sensorMeasurement;
        this.convertMeasurement = convertMeasurement;
    }

    public List<MeasurementWeb> collect() {
        List<Measurement> sensorList = sensorListService.getSensors();

        sensorList = sensorMeasurement.collect(sensorList);

        List<MeasurementWeb>res =  convertMeasurement.convertMeasurementToMeasurementWeb(sensorList);
        return convertMeasurement.convertMeasurementToMeasurementWeb(sensorList);
    }
}
