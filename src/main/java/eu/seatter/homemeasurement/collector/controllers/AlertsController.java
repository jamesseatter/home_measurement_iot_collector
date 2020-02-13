package eu.seatter.homemeasurement.collector.controllers;

import eu.seatter.homemeasurement.collector.model.SensorRecord;
import eu.seatter.homemeasurement.collector.services.cache.AlertCacheService;
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
public class AlertsController {

    private final AlertCacheService cacheService;

    public AlertsController(AlertCacheService cache) {
        this.cacheService = cache;
    }

    @SuppressWarnings("SameReturnValue")
    @RequestMapping(value="/alert")
    public String index(final Model model) {
        ZoneId zoneId= ZoneId.of("Europe/Zurich");

        Map<String, List<SensorRecord>> allSortedAlerts = cacheService.getAllSorted();
        for(String id : allSortedAlerts.keySet()) {
            allSortedAlerts.get(id).forEach((srec) -> srec.setMeasureTimeUTC(srec.getMeasureTimeUTC().withZoneSameInstant(zoneId)));
        }

        model.addAttribute("allalerts", allSortedAlerts);
        return "alert";
    }
}
