package eu.seatter.homemeasurement.collector.controllers;

import eu.seatter.homemeasurement.collector.cache.MeasurementCacheImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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

        model.addAttribute("measurements", cache.getAll());

        return "index";
    }
}
