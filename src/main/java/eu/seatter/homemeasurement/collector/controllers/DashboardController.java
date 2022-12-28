package eu.seatter.homemeasurement.collector.controllers;

import eu.seatter.homemeasurement.collector.converter.ConvertMeasurement;
import eu.seatter.homemeasurement.collector.model.Measurement;
import eu.seatter.homemeasurement.collector.model.MeasurementWeb;
import eu.seatter.homemeasurement.collector.services.MeasureNow;
import eu.seatter.homemeasurement.collector.services.cache.MeasurementCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
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
    @Value("${spring.profiles.active:Unknown}")
    private String activeProfile;
    @Autowired
    private Environment environment;
    private final MeasurementCacheService cacheService;
    private final MeasureNow measureNow;

    private final ConvertMeasurement convertMeasurement;

    public DashboardController(MeasurementCacheService cache, MeasureNow measureNow, ConvertMeasurement convertMeasurement) {
        this.cacheService = cache;
        this.measureNow = measureNow;
        this.convertMeasurement = convertMeasurement;
    }

    @SuppressWarnings("SameReturnValue")
    @RequestMapping(value={"/", "/index"})
    public String index(final Model model) {
        setAllMeasurements(model);
        setLiveMeasurements(model);
        setEnvironment(model);

        return "index";
    }

    private void setAllMeasurements(Model m) {
        Map<String, List<Measurement>> allSortedMeasurements = cacheService.getAllSorted();
        for(String id : allSortedMeasurements.keySet()) {
            allSortedMeasurements.get(id).forEach(srec -> srec.setMeasureTimeUTC(srec.getMeasureTimeUTC()));
        }
        Map<String, List<MeasurementWeb>> allSortedMeasurementsWeb = convertMeasurement.convertMeasurementToMeasurementWeb(allSortedMeasurements);

        m.addAttribute("allMeasurements", allSortedMeasurementsWeb);
        m.addAttribute("view_days","");
    }

    private void setLiveMeasurements(Model m) {
        List<MeasurementWeb> liveMeasurements = Collections.unmodifiableList(measureNow.collect());

        m.addAttribute("liveMeasurements", liveMeasurements);
    }

    private void setEnvironment(Model m) {
        if(activeProfile.equalsIgnoreCase("dev")) {
            m.addAttribute("page_title", "Heating System (dev)");
            m.addAttribute("header_navbar_bgcolour", "#D15023");
        } else if(activeProfile.equalsIgnoreCase("qa")) {
            m.addAttribute("page_title", "Heating System (qa)");
            m.addAttribute("header_navbar_bgcolour", "#236AD1");
        } else {
            m.addAttribute("page_title", "Heating System");
            m.addAttribute("header_navbar_bgcolour", "#CED4DD");
        }
    }
}