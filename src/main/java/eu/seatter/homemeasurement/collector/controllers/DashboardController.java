package eu.seatter.homemeasurement.collector.controllers;

import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.services.cache.MeasurementCacheService;
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
@SuppressWarnings("DuplicatedCode")
@Controller
public class DashboardController {

    private final MeasurementCacheService cacheService;

    public DashboardController(MeasurementCacheService cache) {
        this.cacheService = cache;
    }

    @SuppressWarnings("SameReturnValue")
    @RequestMapping(value={"/", "/index"})
    public String index(final Model model) {
        ZoneId zoneId= ZoneId.of("Europe/Zurich");

        Map<String, List<Measurement>> allSortedMeasurements = cacheService.getAllSorted();
        for(String id : allSortedMeasurements.keySet()) {
            allSortedMeasurements.get(id).forEach((srec) -> srec.setMeasureTimeUTC(srec.getMeasureTimeUTC().withZoneSameInstant(zoneId)));
        }

        model.addAttribute("allMeasurements", allSortedMeasurements);
        model.addAttribute("view_days","");
        return "index";
    }
}
