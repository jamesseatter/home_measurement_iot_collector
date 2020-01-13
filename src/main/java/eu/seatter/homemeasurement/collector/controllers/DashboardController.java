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

        Map<String, List<SensorRecord>> dataset = cacheService.getAllSorted();
        for(String id : dataset.keySet()) {
            dataset.get(id).forEach((srec) -> srec.setMeasureTimeUTC(srec.getMeasureTimeUTC().withZoneSameInstant(zoneId)));
        }

        model.addAttribute("measurements", dataset);
        return "index";
    }
}
