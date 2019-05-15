package eu.seatter.homemeasurement.collector.controllers;

import eu.seatter.homemeasurement.collector.cache.MeasurementCacheImpl;
import eu.seatter.homemeasurement.collector.model.SensorRecord;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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

    @RequestMapping("/chart")
    public String measurementChart(final Model model) {

        model.addAttribute("sensors", cache.getSensorIds());
        for(String sensorId : cache.getSensorIds()) {
            model.addAttribute(sensorId, cache.getAllBySensorId(sensorId));
        }

        return "chart";
    }

//    @RequestMapping(value="/chart/data", produces = "application/json")
//    public List<ArrayList<Double>> getChartData() {
//        List<String> sensors = cache.getSensorIds();
//
//
//        Map<String,List<SensorRecord>> rawdata = cache.getAll();
//
//        List<ArrayList<String>> chartData = new ArrayList<>();
//        ArrayList<String> dataElements = new ArrayList<>();
//        dataElements.addAll(sensors);
//        chartData.add(dataElements);
//
//
//        sensorSB.deleteCharAt(-1);
//        chartData.add(new ArrayList<java.lang.String>(sensorSB.toString()));
//
//        return chartData;
//    }

    @RequestMapping("/chart/data/{sensorid}")
    public List<SensorRecord> getChartDatabySensorId(@PathVariable("sensorid") String sensorid) {
        return cache.getAllBySensorId(sensorid);
    }
}
