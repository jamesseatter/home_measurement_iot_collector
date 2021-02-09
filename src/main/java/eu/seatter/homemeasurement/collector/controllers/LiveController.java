package eu.seatter.homemeasurement.collector.controllers;

import eu.seatter.homemeasurement.collector.model.MeasurementWeb;
import eu.seatter.homemeasurement.collector.services.MeasureNow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 08/02/2021
 * Time: 23:24
 */
@Controller
public class LiveController {
    @Value("${spring.profiles.active:Unknown}")
    private String activeProfile;

    private final MeasureNow measureNow;

    public LiveController(MeasureNow measureNow) {
        this.measureNow = measureNow;
    }

    @SuppressWarnings("SameReturnValue")
    @RequestMapping(value={"/live"})
    public String index(final Model model) {

        List<MeasurementWeb> liveMeasurements = Collections.unmodifiableList(measureNow.collect());

        model.addAttribute("liveMeasurements", liveMeasurements);

        if(!activeProfile.equalsIgnoreCase("prod")) {
            model.addAttribute("title_postfix", "(" + activeProfile + ")");
        }
        return "live";
    }
}
