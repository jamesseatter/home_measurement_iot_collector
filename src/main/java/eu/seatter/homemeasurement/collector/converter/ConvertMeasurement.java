package eu.seatter.homemeasurement.collector.converter;

import eu.seatter.homemeasurement.collector.model.Sensor;
import eu.seatter.homemeasurement.collector.model.SensorWeb;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 22/12/2022
 * Time: 08:48
 */
@Component
public class ConvertMeasurement {

    public List<SensorWeb> convertMeasurementToMeasurementWeb(@NotNull List<Sensor> measurement) {

        List<SensorWeb> measurementWeb = new ArrayList<>();
        for (Sensor m : measurement) {
            measurementWeb.add(convertMeasurementToMeasurementWeb(m));
        }
        return measurementWeb;
    }

    public Map<String,List<SensorWeb>> convertMeasurementToMeasurementWeb(@NotNull Map<String,List<Sensor>> measurement) {

        final Map<String,List<SensorWeb>> mweb = new LinkedHashMap<>();

        for(String id : measurement.keySet()) {
            mweb.put(id,convertMeasurementToMeasurementWeb(measurement.get(id)));
        }
        return mweb;
    }

    public SensorWeb convertMeasurementToMeasurementWeb(@NotNull Sensor m) {
        return SensorWeb.builder()
                .sensorid(m.getSensorid())
                .title(m.getTitle())
                .shorttitle(m.getShortTitle())
                .description(m.getDescription())
                .measureTimeUTC(m.getMeasureTimeUTC())
                .measurementUnit(m.getMeasurementUnit())
                .measurementadjustmentvalue(m.getMeasurementadjustmentvalue())
                .low_threshold(m.getLow_threshold())
                .high_threshold(m.getHigh_threshold())
                .value(m.getValue())
                .build();
    }
}
