package eu.seatter.homemeasurement.collector.controllers;

import eu.seatter.homemeasurement.collector.cache.MeasurementCacheImpl;
import eu.seatter.homemeasurement.collector.model.SensorRecord;
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

    private MeasurementCacheImpl cache;

    public DashboardController(MeasurementCacheImpl cache) {
        this.cache = cache;
    }

    @RequestMapping("/")
    public String index(final Model model) {
        ZoneId zoneId= ZoneId.of("Europe/Zurich");

        Map<String, List<SensorRecord>> dataset = cache.getAll();
        for(Map.Entry<String, List<SensorRecord>> entry: dataset.entrySet()) {
            System.out.println(entry.getKey() + " => " + entry.getValue());
            List<SensorRecord> sr = entry.getValue();
            for(SensorRecord s : sr) {
               s.setMeasureTimeUTC(s.getMeasureTimeUTC().atZone(zoneId).toLocalDateTime());
            }
        }


        model.addAttribute("measurements", dataset);
        return "index";
    }
}
