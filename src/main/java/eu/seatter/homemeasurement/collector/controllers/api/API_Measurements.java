package eu.seatter.homemeasurement.collector.controllers.api;

import eu.seatter.homemeasurement.collector.converter.ConvertMeasurement;
import eu.seatter.homemeasurement.collector.model.Sensor;
import eu.seatter.homemeasurement.collector.model.SensorWeb;
import eu.seatter.homemeasurement.collector.services.cache.MeasurementCacheService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 21/12/2022
 * Time: 22:34
 */
@RestController
@RequestMapping("/api/")
public class API_Measurements
{
    MeasurementCacheService cacheService;
    ConvertMeasurement convertMeasurement;

    public API_Measurements (MeasurementCacheService measurementCacheService, ConvertMeasurement convertMeasurement) {
        this.cacheService = measurementCacheService;
        this.convertMeasurement = convertMeasurement;
    }

    @GetMapping("/measurements")
    public Map<String, List<SensorWeb>> measurements(){
        Map<String,List<Sensor>> measurements = cacheService.getAllSorted();

        return convertMeasurement.convertMeasurementToMeasurementWeb(measurements);
    }

    @GetMapping("/measurementsforchart")
    public Map<String, List<Sensor>> measurementsForChart(){
        Map<String,List<Sensor>> m = cacheService.getAllSorted();



        return cacheService.getAllSorted();
    }
}
