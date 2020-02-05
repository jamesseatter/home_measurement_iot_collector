package eu.seatter.homemeasurement.collector.controllers;

import eu.seatter.homemeasurement.collector.model.SensorRecord;
import eu.seatter.homemeasurement.collector.services.cache.CacheService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 09/05/2019
 * Time: 09:17
 */
@Controller
public class DashboardController {

    private final CacheService cacheService;

    public DashboardController(CacheService cache) {
        this.cacheService = cache;
    }

    @SuppressWarnings("SameReturnValue")
    @RequestMapping("/")
    public String index(final Model model) {
        ZoneId zoneId= ZoneId.of("Europe/Zurich");

        Map<String, List<SensorRecord>> allSortedMeasurements = cacheService.getAllSorted();
        for(String id : allSortedMeasurements.keySet()) {
            allSortedMeasurements.get(id).forEach((srec) -> srec.setMeasureTimeUTC(srec.getMeasureTimeUTC().withZoneSameInstant(zoneId)));
        }

//        Map<String, SensorRecord> lastMeasurementAllSensors = new HashMap<>();
//        List<String> sensorIds = cacheService.getSensorIds();
//        for(String id : cacheService.getSensorIds()) {
//            lastMeasurementAllSensors.put(id, (cacheService.getLastBySensorId(id,1).get(0)));
//        }
//
//        for(String id : lastMeasurementAllSensors.keySet()) {
//            lastMeasurementAllSensors.get(id).setMeasureTimeUTC(lastMeasurementAllSensors.get(id).getMeasureTimeUTC().withZoneSameInstant(zoneId));
//        }

        model.addAttribute("allMeasurements", allSortedMeasurements);
//        model.addAttribute("lastMeasurements", allSortedMeasurements);
        return "index";
    }
}
