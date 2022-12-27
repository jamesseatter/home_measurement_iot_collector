package eu.seatter.homemeasurement.collector.controllers.api;

import eu.seatter.homemeasurement.collector.converter.ConvertMeasurement;
import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.model.MeasurementWeb;
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
    public Map<String, List<MeasurementWeb>> measurements(){
        Map<String,List<Measurement>> measurements = cacheService.getAllSorted();

        return convertMeasurement.convertMeasurementToMeasurementWeb(measurements);
    }

    @GetMapping("/measurementsforchart")
    public Map<String, List<Measurement>> measurementsForChart(){
        Map<String,List<Measurement>> m = cacheService.getAllSorted();



        return cacheService.getAllSorted();
    }
}
